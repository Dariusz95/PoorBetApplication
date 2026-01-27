package com.poorbet.teams.service;

import com.poorbet.teams.dto.TeamShortDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.exception.TeamNotFoundException;
import com.poorbet.teams.mapper.TeamMapper;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;

    @Override
    public List<TeamStatsDto> getStatsByIds(List<UUID> ids) {
        return teamRepository.findAllById(ids)
                .stream()
                .map(team -> new TeamStatsDto(
                        team.getId(),
                        team.getName(),
                        team.getAttackPower(),
                        team.getDefencePower()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamStatsDto> findRandomTeams(Integer count) {
        List<TeamStatsDto> teams = teamRepository.findRandomTeams(count);

        if (teams.size() < count) {
            throw new IllegalStateException("Not enough teams in database");
        }

        return teams;
    }

    @Cacheable(value = "teams", key = "#id")
    @Transactional(readOnly = true)
    @Override
    public TeamShortDto getById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        return TeamMapper.toTeamShortDto(team);
    }
}
