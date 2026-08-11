package com.poorbet.matchservice.team.exception;


public class TeamAlreadyExistsException extends RuntimeException {
    public TeamAlreadyExistsException() {
        super("User already has a team");
    }
}
