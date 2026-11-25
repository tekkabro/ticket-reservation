package com.example.ticketreservation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReservationRequest(
        @NotBlank String eventId,
        @Min(value = 1, message = "seats must be between 1 and 10")
        @Max(value = 10, message = "seats must be between 1 and 10") int seats) {}
