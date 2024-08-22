package com.example.authenticationauthorization.repository;

import com.example.authenticationauthorization.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> getUserByUserId(Long id);
    Boolean existsByUserName(String userName);
    Optional<User>  findByVerificationToken(String token);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
    List<User> findAllByOrderByCreatedAtDesc();
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.verified = false AND u.createdAt <= :cutoffDate")
    void deleteUsersNotVerifiedOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
