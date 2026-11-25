package com.example.ticketreservation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "events")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Event {
    @Id
    private String id;
    private String name;
    private String description;
    private String venue;
    private LocalDateTime eventDate;
    private int totalSeats;
    private int availableSeats;
    private double ticketPrice;
    private String organizerUsername;
}
