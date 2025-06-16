package com.example.furniturestore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.furniturestore.model.User;
import com.example.furniturestore.repository.UserRepository;
import com.example.furniturestore.security.JwtUtil;
import com.example.furniturestore.service.LoginAttemptService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    public static class RegisterRequest {
        public String name;
        public String email;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        User user = new User(request.name, request.email,
                passwordEncoder.encode(request.password), "USER");
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (loginAttemptService.isBlocked(request.email)) {
            return ResponseEntity.status(429).body("Too many login attempts");
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email, request.password));
            loginAttemptService.loginSucceeded(request.email);
            User user = userRepository.findByEmail(request.email).orElse(null);
            String role = user != null ? user.getRole() : "USER";
            String token = jwtUtil.generateToken(request.email, role);
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (AuthenticationException ex) {
            loginAttemptService.loginFailed(request.email);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    public static class TokenResponse {
        public String token;
        public TokenResponse(String token) {
            this.token = token;
        }
    }
}
