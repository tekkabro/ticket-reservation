package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Event;

import java.time.LocalDateTime;

public record EventResponse(String id, String name, String description, String venue,
                            LocalDateTime eventDate, int totalSeats, int availableSeats,
                            double ticketPrice, String organizerUsername) {
    public static EventResponse from(Event e) {
        return new EventResponse(e.getId(), e.getName(), e.getDescription(), e.getVenue(),
                e.getEventDate(), e.getTotalSeats(), e.getAvailableSeats(),
                e.getTicketPrice(), e.getOrganizerUsername());
    }
}
