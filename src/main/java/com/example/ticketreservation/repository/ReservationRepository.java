package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByUsername(String username);
    List<Reservation> findByEventIdIn(Collection<String> eventIds);
    boolean existsByEventIdAndStatus(String eventId, ReservationStatus status);
}
