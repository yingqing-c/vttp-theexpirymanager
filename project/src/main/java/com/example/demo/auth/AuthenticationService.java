package com.example.demo.auth;

import com.example.demo.auth.models.User;
import com.example.demo.auth.repositories.UserRepository;
import com.example.demo.updates.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    EmailUtil emailUtil;

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean registerUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        sendRegistrationEmail(user.getEmail());
        return true;
    }

    public void sendRegistrationEmail(String recipientEmail) {
        String subject = "Registration Confirmation Email";
        String body = "<h1>Welcome to The Expiry Manager!</h1>";
        emailUtil.sendEmail(recipientEmail, subject, body);
    }

    public boolean checkLogin(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return false;
        }
        if (encoder.matches(password, user.get().getPassword())) {
            return true;
        }
        return false;
    }
}
