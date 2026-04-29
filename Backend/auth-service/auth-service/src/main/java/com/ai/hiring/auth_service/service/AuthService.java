package com.ai.hiring.auth_service.service;

import com.ai.hiring.auth_service.dto.*;
import com.ai.hiring.auth_service.model.PasswordResetToken;
import com.ai.hiring.auth_service.model.User;
import com.ai.hiring.auth_service.repository.PasswordResetTokenRepository;
import com.ai.hiring.auth_service.repository.UserRepository;
import com.ai.hiring.auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;



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
    // Forgot Password — OTP bhejo
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Purana OTP delete karo
        passwordResetTokenRepository.deleteByEmail(email);

        // Naya OTP generate karo
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        // OTP save karo — 15 min expiry
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Email bhejo
        emailService.sendOtpEmail(email, otp);

        return "OTP sent to your email!";
    }

    // Reset Password — OTP verify karke password change karo
    public String resetPassword(ResetPasswordRequest request) {

        // OTP verify karo
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByEmailAndOtpAndUsedFalse(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP!"));

        // Expiry check karo
        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired!");
        }

        // Password match check karo
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        // Password update karo
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // OTP mark as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Email bhejo
        emailService.sendPasswordChangeEmail(request.getEmail());

        return "Password reset successfully!";
    }

    // Change Password — Logged in user
    public String changePassword(String email, ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Current password verify karo
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect!");
        }

        // New password match check karo
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        // Password update karo
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Email bhejo
        emailService.sendPasswordChangeEmail(email);

        return "Password changed successfully!";
    }
}
