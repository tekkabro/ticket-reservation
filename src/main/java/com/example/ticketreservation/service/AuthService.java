package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.*;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.model.Role;
import com.example.ticketreservation.repository.UserRepository;
import com.example.ticketreservation.security.JwtService;
import com.example.ticketreservation.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        UserDetails details = (UserDetails) auth.getPrincipal();
        AppUser user = userRepository.findByUsername(details.getUsername())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        String token = jwtService.generateToken(details);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(),
                user.getUsername(), user.getFullName(), user.getRole());
    }

    public UserResponse register(RegisterRequest req) {
        Role requested = req.role() == null ? Role.CUSTOMER : req.role();
        if (requested != Role.CUSTOMER) {
            Authentication caller = SecurityContextHolder.getContext().getAuthentication();
            if (!SecurityUtils.isAdmin(caller)) {
                throw new ApiException(HttpStatus.FORBIDDEN,
                        "Only an ADMIN can create ADMIN or ORGANIZER accounts");
            }
        }
        if (userRepository.existsByUsername(req.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists: " + req.username());
        }
        AppUser saved = userRepository.save(AppUser.builder()
                .username(req.username())
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .role(requested)
                .build());
        return toResponse(saved);
    }

    public UserResponse me(String username) {
        return userRepository.findByUsername(username).map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(AppUser u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole());
    }
}
