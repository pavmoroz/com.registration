package com.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.registration.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
