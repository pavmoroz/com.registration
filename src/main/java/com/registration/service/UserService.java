package com.registration.service;

import com.registration.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.registration.exception.EmailExistsException;
import com.registration.model.PasswordResetToken;
import com.registration.model.User;
import com.registration.model.VerificationToken;
import com.registration.repository.PasswordResetTokenRepository;
import com.registration.repository.UserRepository;
import com.registration.repository.VerificationTokenRepository;
import java.util.UUID;


import javax.transaction.Transactional;

@Service
@Transactional
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository, PasswordResetTokenRepository passwordResetTokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = tokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUserAccount(UserDto dto) throws EmailExistsException{

        if(emailExists(dto.getEmail())){
            throw new EmailExistsException("There is already such email in a database: " + dto.getEmail());
        }

        User registered = new User(dto.getUsername(), dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()), "USER", "");
        return userRepository.save(registered);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken);
    }

    @Override
    public void deleteVerificationToken(long id) {
        verificationTokenRepository.deleteById(id);
    }

    @Override
    public User getUser(String verificationToken){
        final VerificationToken token = verificationTokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public VerificationToken generateNewVerificationToken(String existingToken){
        VerificationToken vToken = verificationTokenRepository.findByToken(existingToken);
        vToken.updateToken(UUID.randomUUID()
                .toString());
        vToken = verificationTokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public User findUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token){
        PasswordResetToken resetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(resetToken);

    }

    @Override
    public void changeUserPassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private boolean emailExists(String email){
        User user = userRepository.findByEmail(email);
        return user != null;
    }
}