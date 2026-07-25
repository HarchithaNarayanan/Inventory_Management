package com.inventory.service;

import com.inventory.dto.AuthResponseDto;
import com.inventory.dto.LoginRequestDto;
import com.inventory.dto.RegisterRequestDto;
import com.inventory.entity.User;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestDto request) {

        // Validate email uniqueness
        if (userRepository.existsByEmailId(request.getEmailId())) {
            throw new DuplicateResourceException("User", "emailId", request.getEmailId());
        }

        // Determine the role - Default to ROLE_STAFF
        User.Role role;
        try {
            role = (request.getRole() != null && !request.getRole().isBlank())
                    ? User.Role.valueOf(request.getRole())
                    : User.Role.ROLE_STAFF;
        } catch (IllegalArgumentException e) {
            role = User.Role.ROLE_STAFF;
        }

        // Build the User entity mapping employeeName
        User user = User.builder()
                .emailId(request.getEmailId())
                .password(passwordEncoder.encode(request.getPassword()))
                .employeeName(request.getEmployeeName())
                .role(role)
                .build();

        user = userRepository.save(user);

        // Generate a JWT token for the new user
        String token = jwtUtil.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .emailId(user.getEmailId())
                .role(user.getRole().name())
                .employeeName(user.getEmployeeName())
                .expiresIn(jwtUtil.getJwtExpiration())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto request) {
        // Authenticate via Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmailId(), request.getPassword())
        );

        // Fetch user object
        User user = userRepository.findByEmailId(request.getEmailId())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        return AuthResponseDto.builder()
                .token(token)
                .emailId(user.getEmailId())
                .role(user.getRole().name())
                .employeeName(user.getEmployeeName())
                .expiresIn(jwtUtil.getJwtExpiration())
                .build();
    }
}
