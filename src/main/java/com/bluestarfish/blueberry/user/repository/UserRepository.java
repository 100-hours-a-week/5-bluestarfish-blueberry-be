package com.bluestarfish.blueberry.user.repository;

import com.bluestarfish.blueberry.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname);
}
