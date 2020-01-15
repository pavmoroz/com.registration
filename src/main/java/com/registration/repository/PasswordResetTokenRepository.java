package com.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.registration.model.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
