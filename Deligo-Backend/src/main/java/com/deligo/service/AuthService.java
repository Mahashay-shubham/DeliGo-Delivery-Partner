package com.deligo.service;

import com.deligo.dto.auth.AuthResponse;
import com.deligo.dto.auth.LoginRequest;
import com.deligo.dto.auth.RegisterRequest;
import com.deligo.dto.auth.UserResponse;
import com.deligo.entity.User;
import com.deligo.entity.UserRole;
import com.deligo.exception.ConflictException;
import com.deligo.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final String bootstrapAdminEmail;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       @Value("${app.bootstrap-admin-email}") String bootstrapAdminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.bootstrapAdminEmail = bootstrapAdminEmail.trim().toLowerCase();
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
        user.setRole(email.equals(bootstrapAdminEmail) && !bootstrapAdminEmail.isBlank() ? UserRole.ADMIN : UserRole.CUSTOMER);
        User savedUser = userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(savedUser.getEmail()), UserResponse.from(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, request.password())
        );
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return new AuthResponse(jwtService.generateToken(user.getEmail()), UserResponse.from(user));
    }
}
