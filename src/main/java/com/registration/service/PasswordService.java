package com.registration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.registration.model.PasswordResetToken;
import com.registration.model.User;
import com.registration.repository.PasswordResetTokenRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@Transactional
public class PasswordService {

    private final PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    public PasswordService(PasswordResetTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public String validatePasswordResetToken(long id, String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getId() != id)) {
            return "invalidToken";
        }

        if(passToken.getExpirationTime().isBefore(LocalDateTime.now())){
            return "expired";
        }

        final User user = passToken.getUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user,
        null, Arrays.asList(new SimpleGrantedAuthority("PASSWORD_CHANGED")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }
}
