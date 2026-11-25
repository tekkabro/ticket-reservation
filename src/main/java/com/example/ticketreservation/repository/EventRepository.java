package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByNameContainingIgnoreCase(String name);
    List<Event> findByOrganizerUsername(String organizerUsername);
}
