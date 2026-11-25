package com.example.ticketreservation.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AppUser {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;   // BCrypt hash
    private String fullName;
    private Role role;
}
