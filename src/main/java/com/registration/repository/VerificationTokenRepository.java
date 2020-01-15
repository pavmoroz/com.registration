package com.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.registration.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken deleteById(long id);


}
