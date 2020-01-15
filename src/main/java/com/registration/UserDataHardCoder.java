package com.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.registration.model.User;
import com.registration.repository.UserRepository;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDataHardCoder implements CommandLineRunner {
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDataHardCoder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        User bob = new User("bob","bob@gmail.com", passwordEncoder.encode("123"), "USER", "",true);
        User sara = new User("sara","sara@gmail.com" ,passwordEncoder.encode("456"), "ADMIN", "",true);
        User mara = new User("mara","mara@gmail.com" ,passwordEncoder.encode("789"), "ADMIN", "",false);
//        User p = new User("p","morozpavel13@gmail.com" ,passwordEncoder.encode("0"), "ADMIN", "",true);

        List<User> users = Arrays.asList(bob, sara, mara);
        userRepository.saveAll(users);
    }
}