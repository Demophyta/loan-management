package com.demo.loan.management.repository;

import com.demo.loan.management.model.PasswordResetToken;
import com.demo.loan.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByOtp(String otp);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
}
