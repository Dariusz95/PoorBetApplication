package com.poorbet.teams.service;

import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.exception.TeamNotFoundException;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;


    public TeamStatsDto getStats(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        return new TeamStatsDto(
                team.getId(),
                team.getName(),
                team.getAttackPower(),
                team.getDefencePower()
        );
    }
}
