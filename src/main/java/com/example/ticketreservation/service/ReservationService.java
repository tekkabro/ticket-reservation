package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.dto.ReservationResponse;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public ReservationResponse book(ReservationRequest req, String username) {
        Event event = eventService.getEntity(req.eventId());
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.CONFLICT, "This event has already taken place");
        }
        if (!eventService.takeSeats(event.getId(), req.seats())) {
            throw new ApiException(HttpStatus.CONFLICT, "Not enough seats available");
        }
        try {
            Reservation r = Reservation.builder()
                    .reservationCode("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .eventId(event.getId())
                    .eventName(event.getName())
                    .username(username)
                    .seats(req.seats())
                    .totalAmount(req.seats() * event.getTicketPrice())
                    .status(ReservationStatus.CONFIRMED)
                    .createdAt(LocalDateTime.now())
                    .build();
            return ReservationResponse.from(reservationRepository.save(r));
        } catch (RuntimeException ex) {
            eventService.releaseSeats(event.getId(), req.seats());   // roll back the seats
            throw ex;
        }
    }

    public List<ReservationResponse> myReservations(String username) {
        return reservationRepository.findByUsername(username).stream().map(ReservationResponse::from).toList();
    }

    /** ADMIN sees everything; ORGANIZER sees reservations for their own events only. */
    public List<ReservationResponse> listForStaff(String username, boolean isAdmin) {
        List<Reservation> list;
        if (isAdmin) {
            list = reservationRepository.findAll();
        } else {
            List<String> ids = eventRepository.findByOrganizerUsername(username).stream().map(Event::getId).toList();
            list = ids.isEmpty() ? List.of() : reservationRepository.findByEventIdIn(ids);
        }
        return list.stream().map(ReservationResponse::from).toList();
    }

    public ReservationResponse cancel(String id, String username, boolean isAdmin) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reservation not found: " + id));
        if (!isAdmin && !r.getUsername().equals(username)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only cancel your own reservations");
        }
        if (r.getStatus() == ReservationStatus.CANCELLED) {
            throw new ApiException(HttpStatus.CONFLICT, "Reservation is already cancelled");
        }
        r.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(r);
        eventService.releaseSeats(r.getEventId(), r.getSeats());
        return ReservationResponse.from(r);
    }
}
