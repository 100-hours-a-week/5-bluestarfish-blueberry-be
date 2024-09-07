package com.bluestarfish.blueberry.room.service;

import com.bluestarfish.blueberry.common.dto.UserRoomRequest;
import com.bluestarfish.blueberry.common.dto.UserRoomResponse;
import com.bluestarfish.blueberry.common.entity.UserRoom;
import com.bluestarfish.blueberry.common.exception.UserRoomException;
import com.bluestarfish.blueberry.common.repository.UserRoomRepository;
import com.bluestarfish.blueberry.common.s3.S3Uploader;
import com.bluestarfish.blueberry.room.dto.RoomDetailResponse;
import com.bluestarfish.blueberry.room.dto.RoomRequest;
import com.bluestarfish.blueberry.room.dto.RoomResponse;
import com.bluestarfish.blueberry.room.entity.Room;
import com.bluestarfish.blueberry.room.exception.RoomException;
import com.bluestarfish.blueberry.room.repository.RoomRepository;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    @Value("${room.image.storage}")
    private String roomThumbnailStorage;

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Override
    public void createRoom(RoomRequest roomRequest) {

        String imagePath = null;
        MultipartFile multipartFile = roomRequest.getThumbnail();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            imagePath = s3Uploader.upload(multipartFile, roomThumbnailStorage);
        }

        Room room = roomRepository.save(roomRequest.toEntity(imagePath));
        User user = userRepository.findByIdAndDeletedAtIsNull(roomRequest.getUserId())
                .orElseThrow(() -> new RoomException("User not found with id: " + roomRequest.getUserId(), HttpStatus.NOT_FOUND));

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

    @Override
    public RoomDetailResponse getRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
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
    public void deleteRoomById(Long id) {
        Room room = roomRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RoomException("Room not found with id: " + id, HttpStatus.NOT_FOUND));
        room.setDeletedAt(LocalDateTime.now());
    }

    @Override
    public void entranceRoom(Long roomId, Long userId, UserRoomRequest userRoomRequest) {
        boolean isExisted = userRoomRepository.findByRoomIdAndUserId(roomId, userId).isPresent();
        if (isExisted) { // 재입장
            UserRoom userRoom = userRoomRepository.findByRoomIdAndUserId(roomId, userId)
                    .orElseThrow(() -> new UserRoomException("UserRoom not found this user id: " + userId, HttpStatus.NOT_FOUND));
            userRoom.setActive(true);
        } else { // 첫 입장
            User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new UserRoomException("User not found this user id: " + userId, HttpStatus.NOT_FOUND));
            Room room = roomRepository.findByIdAndDeletedAtIsNull(roomId)
                    .orElseThrow(() -> new UserRoomException("Room not found this room id: " + roomId, HttpStatus.NOT_FOUND));
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
                .orElseThrow(() -> new UserRoomException("UserRoom not found this user id: " + userId, HttpStatus.NOT_FOUND));
        userRoom.setActive(false);
    }

    @Override
    public int getActiveMemberCount(Long roomId) {
        return userRoomRepository.countActiveMembersByRoomId(roomId);
    }
}
