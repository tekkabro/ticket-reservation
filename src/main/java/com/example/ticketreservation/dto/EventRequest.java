package com.example.ticketreservation.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank String name,
        String description,
        @NotBlank String venue,
        @NotNull @Future LocalDateTime eventDate,
        @Min(value = 1, message = "totalSeats must be at least 1") int totalSeats,
        @PositiveOrZero double ticketPrice) {}
