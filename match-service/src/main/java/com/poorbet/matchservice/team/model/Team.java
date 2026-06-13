package com.poorbet.matchservice.team.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String img;

    @Column(name = "attack_power")
    private int attackPower;

    @Column(name = "defence_power")
    private int defencePower;
}
