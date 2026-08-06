package com.poorbet.matchservice.team.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String img;

    @Column(name = "attack_power")
    private int attackPower;

    @Column(name = "defence_power")
    private int defencePower;

    @Column(name = "user_id", unique = true)
    private UUID userId;
}
