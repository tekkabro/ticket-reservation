package com.example.ticketreservation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reservations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reservationCode;
    private String eventId;
    private String eventName;
    private String username;
    private int seats;
    private double totalAmount;
    private ReservationStatus status;
    private LocalDateTime createdAt;
}
