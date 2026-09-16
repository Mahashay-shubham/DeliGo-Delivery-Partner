package com.deligo.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deligo.dto.auth.AuthResponse;
import com.deligo.dto.auth.ForgotPasswordRequest;
import com.deligo.dto.auth.LoginRequest;
import com.deligo.dto.auth.MessageResponse;
import com.deligo.dto.auth.RegisterRequest;
import com.deligo.dto.auth.ResetPasswordRequest;
import com.deligo.dto.auth.UserResponse;
import com.deligo.entity.User;
import com.deligo.entity.UserRole;
import com.deligo.exception.ConflictException;
import com.deligo.repository.UserRepository;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String GENERIC_RESET_MESSAGE = "If an account exists for this email, a password reset link has been sent.";
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final String bootstrapAdminEmail;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String frontendOrigin;
    private final String mailHost;
    private final String mailFrom;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService,
            @Value("${app.bootstrap-admin-email}") String bootstrapAdminEmail,
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.frontend-origin}") String frontendOrigin,
            @Value("${MAIL_HOST:}") String mailHost,
            @Value("${app.mail-from}") String mailFrom) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bootstrapAdminEmail = bootstrapAdminEmail.trim().toLowerCase();
        this.mailSenderProvider = mailSenderProvider;
        this.frontendOrigin = frontendOrigin.replaceAll("/+$", "");
        this.mailHost = mailHost;
        this.mailFrom = mailFrom;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account already exists for this email address");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(email.equals(bootstrapAdminEmail) && !bootstrapAdminEmail.isBlank() ? UserRole.ADMIN
                : UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(savedUser.getEmail()), UserResponse.from(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, request.password()));
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return new AuthResponse(jwtService.generateToken(user.getEmail()), UserResponse.from(user));
    }

    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.email().trim().toLowerCase()).ifPresent(this::createAndDeliverResetLink);
        return new MessageResponse(GENERIC_RESET_MESSAGE);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        String tokenHash = sha256(request.token());
        User user = userRepository.findForPasswordReset(tokenHash)
                .orElseThrow(() -> new com.deligo.exception.BadRequestException("This password reset link is invalid or has already been used."));

        if (user.getPasswordResetExpiresAt() == null || !user.getPasswordResetExpiresAt().isAfter(Instant.now())) {
            invalidateResetToken(user);
            userRepository.save(user);
            throw new com.deligo.exception.BadRequestException("This password reset link has expired.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        invalidateResetToken(user);
        userRepository.save(user);
        return new MessageResponse("Password reset successfully.");
    }

    private void createAndDeliverResetLink(User user) {
        String rawToken = generateSecureToken();
        user.setPasswordResetTokenHash(sha256(rawToken));
        user.setPasswordResetExpiresAt(Instant.now().plus(RESET_TOKEN_TTL));
        userRepository.save(user);

        String resetUrl = frontendOrigin + "/reset-password?token=" + rawToken;
        if (mailHost == null || mailHost.isBlank()) {
            log.info("DEV ONLY RESET LINK: {}", resetUrl);
            return;
        }

        try {
            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender == null) {
                log.warn("Password reset email could not be sent because no mail sender is configured.");
                return;
            }
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(user.getEmail());
            helper.setSubject("Reset your DeliGo password");
            helper.setText("Use this link to reset your password. It expires in 15 minutes:\n\n" + resetUrl, false);
            mailSender.send(message);
        } catch (Exception exception) {
            log.warn("Password reset email delivery failed.");
        }
    }

    private void invalidateResetToken(User user) {
        user.setPasswordResetTokenHash(null);
        user.setPasswordResetExpiresAt(null);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
