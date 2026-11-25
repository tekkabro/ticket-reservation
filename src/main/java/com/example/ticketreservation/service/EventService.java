package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.EventRequest;
import com.example.ticketreservation.dto.EventResponse;
import com.example.ticketreservation.exception.ApiException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final MongoTemplate mongoTemplate;

    public List<EventResponse> list(String search) {
        List<Event> events = (search == null || search.isBlank())
                ? eventRepository.findAll()
                : eventRepository.findByNameContainingIgnoreCase(search);
        return events.stream().map(EventResponse::from).toList();
    }

    public Event getEntity(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public EventResponse get(String id) { return EventResponse.from(getEntity(id)); }

    public EventResponse create(EventRequest req, String organizerUsername) {
        Event e = Event.builder()
                .name(req.name()).description(req.description()).venue(req.venue())
                .eventDate(req.eventDate())
                .totalSeats(req.totalSeats()).availableSeats(req.totalSeats())
                .ticketPrice(req.ticketPrice())
                .organizerUsername(organizerUsername)
                .build();
        return EventResponse.from(eventRepository.save(e));
    }

    public EventResponse update(String id, EventRequest req, String username, boolean isAdmin) {
        Event e = getEntity(id);
        if (!isAdmin && !username.equals(e.getOrganizerUsername())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can only edit your own events");
        }
        int booked = e.getTotalSeats() - e.getAvailableSeats();
        if (req.totalSeats() < booked) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "totalSeats cannot be lower than the " + booked + " seats already booked");
        }
        e.setName(req.name());
        e.setDescription(req.description());
        e.setVenue(req.venue());
        e.setEventDate(req.eventDate());
        e.setTicketPrice(req.ticketPrice());
        e.setTotalSeats(req.totalSeats());
        e.setAvailableSeats(req.totalSeats() - booked);
        return EventResponse.from(eventRepository.save(e));
    }

    public void delete(String id) {
        Event e = getEntity(id);
        if (reservationRepository.existsByEventIdAndStatus(id, ReservationStatus.CONFIRMED)) {
            throw new ApiException(HttpStatus.CONFLICT, "Event has confirmed reservations and cannot be deleted");
        }
        eventRepository.delete(e);
    }

    /** Atomic: the filter (availableSeats >= seats) and the decrement happen in ONE MongoDB operation,
     *  so two customers can never take the last seats at the same time. */
    public boolean takeSeats(String eventId, int seats) {
        Query q = new Query(Criteria.where("id").is(eventId).and("availableSeats").gte(seats));
        Update u = new Update().inc("availableSeats", -seats);
        return mongoTemplate.updateFirst(q, u, Event.class).getModifiedCount() == 1;
    }

    public void releaseSeats(String eventId, int seats) {
        Query q = new Query(Criteria.where("id").is(eventId));
        mongoTemplate.updateFirst(q, new Update().inc("availableSeats", seats), Event.class);
    }
}
