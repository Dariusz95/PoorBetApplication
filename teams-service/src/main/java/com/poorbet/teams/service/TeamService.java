package com.poorbet.teams.service;

import com.poorbet.teams.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {

    List<Team> findAll();

    Team findById(UUID id);

}
