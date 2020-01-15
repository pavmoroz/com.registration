package com.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.registration.model.User;
import com.registration.model.VerificationToken;
import com.registration.repository.UserRepository;
import com.registration.repository.VerificationTokenRepository;

import java.util.List;

@RestController
public class DataController {

    private UserRepository userRepository;
    // no need?
    private VerificationTokenRepository tokenRepository;

    @Autowired
    public DataController(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @RequestMapping("/showUsers")
    public List<User> users(){
        return userRepository.findAll();
    }

    @RequestMapping("/showTokens")
    public List<VerificationToken> tokens(){
        return tokenRepository.findAll();
    }
}