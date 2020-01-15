package com.registration.service;

import com.registration.dto.UserDto;
import com.registration.model.User;
import com.registration.model.VerificationToken;

public interface IUserService {
    User registerNewUserAccount(UserDto accountDto);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    void deleteVerificationToken(long id);

    User getUser(String verificationToken);

    VerificationToken generateNewVerificationToken(String existingToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    void changeUserPassword(User user, String password);
}
