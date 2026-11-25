package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 3, message = "password must be at least 3 characters") String password,
        @NotBlank String fullName,
        Role role) {}
