package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Role;

public record UserResponse(String id, String username, String fullName, Role role) {}
