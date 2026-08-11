package com.poorbet.matchservice.team.exception;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class TeamNotFoundException extends EntityNotFoundException {

    public TeamNotFoundException(UUID id) {
        super("Team with id " + id + " not found");
    }
}