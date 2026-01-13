package com.poorbet.teams.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "teams")
@Data
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
