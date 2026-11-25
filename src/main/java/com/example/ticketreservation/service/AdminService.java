package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.StatsResponse;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;
import com.example.ticketreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public StatsResponse stats() {
        List<Reservation> confirmed = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED).toList();
        long tickets = confirmed.stream().mapToLong(Reservation::getSeats).sum();
        double revenue = confirmed.stream().mapToDouble(Reservation::getTotalAmount).sum();
        return new StatsResponse(userRepository.count(), eventRepository.count(),
                confirmed.size(), tickets, revenue);
    }
}
