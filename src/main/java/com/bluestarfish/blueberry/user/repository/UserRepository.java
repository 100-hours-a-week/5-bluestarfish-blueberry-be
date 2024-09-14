package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname);

    Optional<User> findByEmailAndDeletedAtIsNull(String Email);

    Optional<User> findByEmailAndDeletedAtIsNotNull(String Email);
}
