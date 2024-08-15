package com.bluestarfish.blueberry.user.service;

import com.bluestarfish.blueberry.user.dto.JoinRequest;
import com.bluestarfish.blueberry.user.dto.UserUpdateRequest;
import com.bluestarfish.blueberry.user.dto.UserResponse;
import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void join(JoinRequest joinRequest) {
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
                .ifPresent(user::setPassword);
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
}
