package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.common.s3.S3Uploader;
import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.user.dto.*;
import com.bluestarfish.blueberry.user.entity.StudyTime;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.StudyTimeRepository;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Value("${user.image.storage}")
    private String userImageStorage;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final S3Uploader s3Uploader;
    private final JWTUtils jwtUtils;

    @Override
    public UserResponse getUserByToken(String accessToken) {
        Long userId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        return UserResponse.from(userRepository.findById(userId).orElseThrow(() -> new UserException("", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void join(JoinRequest joinRequest) {
        userRepository.findByEmailAndDeletedAtIsNull(joinRequest.getEmail())
                .ifPresent(user -> {
                    throw new UserException("The email address already exists", HttpStatus.CONFLICT);
                });

        joinRequest.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        userRepository.save(joinRequest.toEntity());
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
        );

        return UserResponse.from(user);
    }

    @Override
    public void update(
            Long id,
            UserUpdateRequest userUpdateRequest,
            String accessToken
    ) throws IOException {

        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
        );

        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        if (!tokenId.equals(user.getId())) {
            throw new UserException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }

        // FIXME: 이후 리팩토링
        String imagePath = null;
        MultipartFile multipartFile = userUpdateRequest.getProfileImage(); // 빈 객체 or 이미지 파일

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if (user.getProfileImage() != null) {
                imagePath = s3Uploader.updateFile(multipartFile, user.getProfileImage(), userImageStorage);
            }

            if (user.getProfileImage() == null) {
                imagePath = s3Uploader.upload(multipartFile, userImageStorage);
            }
        }


        Optional.ofNullable(userUpdateRequest.getNickname())
                .filter(nickname -> !nickname.isEmpty())
                .ifPresent(user::setNickname);

        Optional.ofNullable(imagePath)
                .ifPresent(user::setProfileImage);

        Optional.ofNullable(userUpdateRequest.getPassword())
                .filter(password -> !password.isEmpty())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
    }

    @Override
    public void withdraw(
            Long id,
            String accessToken
    ) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
                );
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        if (!tokenId.equals(user.getId())) {
            throw new UserException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }

        user.setDeletedAt(LocalDateTime.now());
    }

    @Override
    public void validateNickname(String nickname) {
        userRepository.findByNicknameAndDeletedAtIsNull(nickname)
                .ifPresent(user -> {
                    throw new UserException(nickname + " already in use", HttpStatus.CONFLICT);
                });
    }

    @Override
    public void resetPassword(PasswordResetRequest passwordResetRequest, String accessToken) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(passwordResetRequest.getEmail())
                .orElseThrow(
                        () -> new UserException("A user with " + passwordResetRequest.getEmail() + " not found",
                                HttpStatus.NOT_FOUND)
                );
        Long tokenId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        if (!tokenId.equals(user.getId())) {
            throw new UserException("Not match request ID and login ID", HttpStatus.UNAUTHORIZED);
        }

        user.setPassword(passwordEncoder.encode(passwordResetRequest.getPassword()));
    }

    @Override
    public StudyTimeResponse getStudyTime(Long userId) {

        User user = userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(
                () -> new UserException("A user with " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        Optional<StudyTime> studyTime = studyTimeRepository.findByUserIdAndToday(user.getId());

        if (studyTime.isPresent()) {
            return StudyTimeResponse.from(studyTime.get());
        }

        return StudyTimeResponse.from(
                studyTimeRepository.save(
                        StudyTime.builder()
                                .user(user)
                                .build()
                )
        );
    }

    @Override
    public void updateStudyTime(Long userId, StudyTimeUpdateRequest studyTimeUpdateRequest) {
        userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(
                () -> new UserException("A user with " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        StudyTime studyTime = studyTimeRepository.findByUserIdAndToday(userId).orElseThrow(
                () -> new UserException("Study time data not found", HttpStatus.NOT_FOUND)
        );

        studyTime.setTime(studyTimeUpdateRequest.getTime());
    }

    @Override
    public List<Rank> getRanks(Long userId) {
        userRepository.findByIdAndDeletedAtIsNull(userId).orElseThrow(
                () -> new UserException("A user with " + userId + " not found", HttpStatus.NOT_FOUND)
        );

        List<StudyTime> studyTimes = studyTimeRepository.findRanksTop10Yesterday();

        AtomicInteger order = new AtomicInteger(1);

        List<Rank> ranks = studyTimes.stream()
                .map(studyTime -> Rank.builder()
                        .rank(order.getAndIncrement())
                        .nickname(studyTime.getUser().getNickname())
                        .time(studyTime.getTime())
                        .build())
                .collect(Collectors.toList());

        //FIXME: 이후 수정
        studyTimes = studyTimeRepository.findRanksYesterday(userId);
        for (StudyTime studyTime : studyTimes) {
            if (studyTime.getUser().getId().equals(userId)) {
                ranks.add(
                        Rank.builder()
                                .rank(studyTimes.indexOf(studyTime) + 1)
                                .nickname(studyTime.getUser().getNickname())
                                .time(studyTime.getTime())
                                .build()
                );
            }
        }

        return ranks;
    }
}
