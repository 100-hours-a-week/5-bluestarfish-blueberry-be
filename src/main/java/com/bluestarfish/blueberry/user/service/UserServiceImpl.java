package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.jwt.JWTUtils;
import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.PasswordResetRequest;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.dto.UserUpdateRequest;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;

    @Override
    public UserResponse getUserByToken(String accessToken) {
        Long userId = jwtUtils.getId(URLDecoder.decode(accessToken, StandardCharsets.UTF_8));
        return UserResponse.from(userRepository.findById(userId).orElseThrow(() -> new UserException("", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void join(JoinRequest joinRequest) {
        System.out.println(1111);
        userRepository.findByEmailAndDeletedAtIsNull(joinRequest.getEmail())
                .ifPresent(user -> {
                    throw new UserException("The email address already exists", HttpStatus.CONFLICT);
                });
        System.out.println(1111);
        joinRequest.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        userRepository.save(joinRequest.toEntity());
    }

    @Override
    public UserResponse findById(
            Long id
    ) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
        );

        return UserResponse.from(user);
    }

    @Override
    public void update(
            Long id,
            UserUpdateRequest userUpdateRequest
    ) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
        );

        Optional.ofNullable(userUpdateRequest.getNickname())
                .ifPresent(user::setNickname);

        Optional.ofNullable(userUpdateRequest.getProfileImage())
                .ifPresent(user::setProfileImage);

        Optional.ofNullable(userUpdateRequest.getPassword())
                .ifPresent(password -> user.setPassword(passwordEncoder.encode(password)));
    }

    @Override
    public void withdraw(
            Long id
    ) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(
                        () -> new UserException("A user with " + id + " not found", HttpStatus.NOT_FOUND)
                );

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
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmailAndDeletedAtIsNull(passwordResetRequest.getEmail())
                .orElseThrow(
                        () -> new UserException("A user with " + passwordResetRequest.getEmail() + " not found",
                                HttpStatus.NOT_FOUND)
                );

        user.setPassword(passwordEncoder.encode(passwordResetRequest.getPassword()));
    }
}
