package com.ai.hiring.auth_service.service;

import com.ai.hiring.auth_service.dto.AuthResponse;
import com.ai.hiring.auth_service.dto.LoginRequest;
import com.ai.hiring.auth_service.dto.RegisterRequest;
import com.ai.hiring.auth_service.model.User;
import com.ai.hiring.auth_service.repository.UserRepository;
import com.ai.hiring.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // Email already exists check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Password match check
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Role set
        User.Role role = User.Role.CANDIDATE;
        if (request.getRole() != null &&
                request.getRole().equalsIgnoreCase("RECRUITER")) {
            role = User.Role.RECRUITER;
        }

        // Create User
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        // Token generate
        String token = jwtUtil.generateToken(user.getEmail(),
                user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {

        // User Fetch(user check karo)
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate Token
        String token = jwtUtil.generateToken(user.getEmail(),
                user.getRole().name());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                "Login successful"
        );
    }
}
