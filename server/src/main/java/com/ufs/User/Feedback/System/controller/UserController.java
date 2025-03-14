package com.ufs.User.Feedback.System.controller;

import com.ufs.User.Feedback.System.jwt.JwtToken;
import com.ufs.User.Feedback.System.model.User;
import com.ufs.User.Feedback.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtToken jwtToken;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        if(userService.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }
        PasswordEncoder pss = new BCryptPasswordEncoder();
        user.setPassword(pss.encode(user.getPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> existing = userService.findByUsername(user.getUsername());

        if(existing.isPresent()){
            PasswordEncoder pss = new BCryptPasswordEncoder();
            if(pss.matches(user.getPassword(), existing.get().getPassword())) {
                String token = jwtToken.generateToken(existing.get());
                return ResponseEntity.ok(Map.of("token", token));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
