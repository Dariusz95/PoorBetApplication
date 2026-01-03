package com.poorbet.teams.service;

import com.poorbet.teams.config.MatchesProperties;
import com.poorbet.teams.dto.MatchDto;
import com.poorbet.teams.dto.TeamStatsDto;
import com.poorbet.teams.exception.TeamNotFoundException;
import com.poorbet.teams.mapper.TeamMapper;
import com.poorbet.teams.model.Team;
import com.poorbet.teams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final MatchesProperties matchesProperties;

    @Override
    public List<TeamStatsDto> findRandomTeams(Integer count) {
        int matchesInBatch = Optional.ofNullable(count)
                .orElse(matchesProperties.getInBatch());

        log.info("xxxx in batch - {}", matchesInBatch);
        log.info("xxxxaayuuu count - {}", matchesInBatch);
        int teamsToFetch = matchesInBatch * 2;
        log.info("xxxx teamsToFetch - {}", teamsToFetch);
        List<TeamStatsDto> teams = teamRepository.findRandomTeams(teamsToFetch);

        if (teams.size() < teamsToFetch) {
            throw new IllegalStateException("Not enough teams in database");
        }

        return teams;
    }
}
