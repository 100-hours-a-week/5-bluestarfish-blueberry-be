package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.common.dto.UserRoomRequest;
import com.bluestarfish.blueberry.common.dto.UserRoomResponse;
import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.common.repository.UserRoomRepository;
import com.bluestarfish.blueberry.common.s3.S3Uploader;
import com.bluestarfish.blueberry.exception.CustomException;
import com.bluestarfish.blueberry.exception.ExceptionDomain;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.room.dto.RoomDetailResponse;
import com.bluestarfish.blueberry.room.dto.RoomPasswordRequest;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Value("${room.image.storage}")
    private String roomThumbnailStorage;

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;
    private final S3Uploader s3Uploader;

    @Override
    public void createRoom(RoomRequest roomRequest, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));

        // 스터디룸 썸네일 이미지 처리
        String imagePath = null;
        MultipartFile multipartFile = roomRequest.getThumbnail();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            imagePath = s3Uploader.upload(multipartFile, roomThumbnailStorage);
        }

        // 요청 보낸 유저 확인
        User user = userRepository.findByIdAndDeletedAtIsNull(roomRequest.getUserId())
                .orElseThrow(() -> new CustomException("User not found with id: " + roomRequest.getUserId(), ExceptionDomain.ROOM, HttpStatus.NOT_FOUND));

        if (!tokenId.equals(user.getId())) {
            throw new CustomException("Not match request ID and login ID", ExceptionDomain.ROOM, HttpStatus.UNAUTHORIZED);
        }

        // 유저가 만든 스터디룸 개수 확인
        if(countRoomByUserID(user.getId()) >= 5) {
            throw new CustomException("Too Many Room to Make More Rooms.", ExceptionDomain.ROOM, HttpStatus.FORBIDDEN);
        }

        // 스터디룸 데이터 생성
        Room room = roomRepository.save(roomRequest.toEntity(imagePath));
        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .isHost(true)
                .isActive(false)
                .camEnabled(false)
                .micEnabled(false)
                .speakerEnabled(false)
                .goalTime(new Time(0))
                .dayTime(new Time(0))
                .build();

        userRoomRepository.save(userRoom);
    }

    // 유저가 만든 스터디룸의 개수를 반환
    private int countRoomByUserID(Long userId) {
        List<Room> rooms = roomRepository.findRoomsByUserIdAndIsHost(userId);
        return rooms.size();
    }

    @Override
    public RoomDetailResponse getRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException("Room not found with id: " + id, ExceptionDomain.ROOM, HttpStatus.NOT_FOUND));
        List<UserRoomResponse> userRooms = userRoomRepository.findByRoomIdAndIsActiveTrue(room.getId())
                .stream().map(userRoom -> UserRoomResponse.from(userRoom, userRoom.getUser()))
                .collect(Collectors.toList());
        return RoomDetailResponse.from(room, userRooms);
    }

    @Override
    public Page<RoomResponse> getAllRooms(int page, String keyword, Boolean isCamEnabled) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Direction.DESC, "createdAt"));

        // 이후 QueryDSL or @Query 스타일로 변경 검토
        if (keyword == null && isCamEnabled == null) { // 검색 keyword가 없고, 캠 여부가 전체 인 경우 조회
            return roomRepository.findByDeletedAtIsNull(pageable)
                    .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
        } else if (keyword == null) { // 검색어가 없고, 캠여부가 true or false 인 경우 조회
            if (isCamEnabled) {
                return roomRepository.findByIsCamEnabledAndDeletedAtIsNull(true, pageable)
                        .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
            } else {
                return roomRepository.findByIsCamEnabledAndDeletedAtIsNull(false, pageable)
                        .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
            }
        } else if (isCamEnabled == null) { // 검색어가 있고, 캠 여부가 all 인 경우 조회
            return roomRepository.findByTitleContainingAndDeletedAtIsNull(keyword, pageable)
                    .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
        } else { // 검색어가 있고, 캠 여부가 true or false 인 경우 조회
            if (isCamEnabled) {
                return roomRepository.findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(keyword, true, pageable)
                        .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
            } else {
                return roomRepository.findByTitleContainingAndIsCamEnabledAndDeletedAtIsNull(keyword, false, pageable)
                        .map(room -> RoomResponse.from(room, getActiveMemberCount(room.getId())));
            }
        }
    }

    @Override
    public List<RoomResponse> getMyRooms(Long userId) {
        return roomRepository.findRoomsByUserIdAndIsHost(userId).stream().map(room -> RoomResponse.from(room,
                userRoomRepository.countActiveMembersByRoomId(room.getId()))).collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getVisitedRooms(Long userId) {
        return roomRepository.findRoomsByUserIdAndDeletedAtIsNull(userId).stream().map(room -> RoomResponse.from(room,
                userRoomRepository.countActiveMembersByRoomId(room.getId()))).collect(Collectors.toList());
    }

    @Override
    public void deleteRoomById(Long id, String accessToken) {
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));

        UserRoom userRoom = userRoomRepository.findByRoomIdAndIsHostTrue(id);
        if (!tokenId.equals(userRoom.getUser().getId())) {
            throw new CustomException("Not match request ID and login ID", ExceptionDomain.ROOM, HttpStatus.UNAUTHORIZED);
        }
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CustomException("Room not found with id: " + id, ExceptionDomain.ROOM, HttpStatus.NOT_FOUND));
        room.setDeletedAt(LocalDateTime.now());
    }

    @Override
    public void entranceRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new CustomException("Room not found this room id: " + roomId, ExceptionDomain.ROOM, HttpStatus.NOT_FOUND));
        boolean needPassword = !room.getPassword().isEmpty();
        boolean isExisted = userRoomRepository.findByRoomIdAndUserId(roomId, userId).isPresent();

        if (needPassword) {
            if (userRoomRequest.getPassword() == null || !userRoomRequest.getPassword().equals(room.getPassword())) {
                throw new CustomException("Password is not correct", ExceptionDomain.ROOM, HttpStatus.UNAUTHORIZED);
            }
        }

        if (isExisted) { // 재입장

            UserRoom userRoom = userRoomRepository.findByRoomIdAndUserId(roomId, userId)
                    .orElseThrow(() -> new CustomException("UserRoom not found this user id: " + userId, ExceptionDomain.USERROOM, HttpStatus.NOT_FOUND));
            userRoom.setActive(true);
        } else { // 첫 입장
            User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new CustomException("User not found this user id: " + userId, ExceptionDomain.USERROOM, HttpStatus.NOT_FOUND));
            userRoomRepository.save(UserRoom.builder()
                    .user(user)
                    .room(room)
                    .isHost(userRoomRequest.isHost())
                    .isActive(userRoomRequest.isActive())
                    .camEnabled(userRoomRequest.isCamEnabled())
                    .micEnabled(userRoomRequest.isMicEnabled())
                    .speakerEnabled(userRoomRequest.isSpeakerEnabled())
                    .goalTime(userRoomRequest.getGoalTime())
                    .dayTime(userRoomRequest.getDayTime())
                    .build());
        }
    }

    @Override
    public void exitRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest) {
        UserRoom userRoom = userRoomRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException("UserRoom not found this user id: " + userId, ExceptionDomain.USERROOM, HttpStatus.NOT_FOUND));
        userRoom.setActive(false);
    }

    @Override
    public int getActiveMemberCount(Long roomId) {
        return userRoomRepository.countActiveMembersByRoomId(roomId);
    }

    @Override
    public void checkRoomPassword(RoomPasswordRequest roomPasswordRequest) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(roomPasswordRequest.getRoomId())
                .orElseThrow(() -> new CustomException("Room not found this room id: " + roomPasswordRequest.getRoomId(), ExceptionDomain.ROOM, HttpStatus.NOT_FOUND));

        if (!roomPasswordRequest.getPassword().equals(room.getPassword())) {
            throw new CustomException("Password verification failed.", ExceptionDomain.ROOM, HttpStatus.UNAUTHORIZED);
        }
    }
}
