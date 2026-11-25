package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(String id, String reservationCode, String eventId, String eventName,
                                  String username, int seats, double totalAmount,
                                  ReservationStatus status, LocalDateTime createdAt) {
    public static ReservationResponse from(Reservation r) {
        return new ReservationResponse(r.getId(), r.getReservationCode(), r.getEventId(), r.getEventName(),
                r.getUsername(), r.getSeats(), r.getTotalAmount(), r.getStatus(), r.getCreatedAt());
    }
}
