package com.example.ticketreservation.config;

import com.example.ticketreservation.model.AppUser;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Role;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Seeds the three accounts and sample events on first start. */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUser("admin", "admin123", "System Administrator", Role.ADMIN);
        seedUser("organizer", "organizer123", "Event Organizer", Role.ORGANIZER);
        seedUser("customer", "customer123", "Sample Customer", Role.CUSTOMER);

        if (eventRepository.count() == 0) {
            eventRepository.save(sampleEvent("Colombo Jazz Night", "Live jazz under the stars",
                    "Viharamahadevi Park, Colombo", LocalDateTime.now().plusDays(30), 100, 2500));
            eventRepository.save(sampleEvent("Tech Summit 2027", "Annual technology conference",
                    "BMICH, Colombo", LocalDateTime.now().plusDays(60), 200, 5000));
            eventRepository.save(sampleEvent("Kelaniya Food Festival", "Street food from across the island",
                    "Kelaniya", LocalDateTime.now().plusDays(45), 150, 1000));
        }
    }

    private void seedUser(String username, String rawPassword, String fullName, Role role) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(AppUser.builder().username(username)
                    .password(passwordEncoder.encode(rawPassword)).fullName(fullName).role(role).build());
        }
    }

    private Event sampleEvent(String name, String desc, String venue, LocalDateTime date, int seats, double price) {
        return Event.builder().name(name).description(desc).venue(venue).eventDate(date)
                .totalSeats(seats).availableSeats(seats).ticketPrice(price)
                .organizerUsername("organizer").build();
    }
}
