package com.poorbet.matchservice.team.service;

import com.poorbet.matchservice.team.dto.TeamShortDto;
import com.poorbet.matchservice.team.dto.TeamStatsDto;
import com.poorbet.matchservice.team.exception.TeamNotFoundException;
import com.poorbet.matchservice.team.mapper.TeamMapper;
import com.poorbet.matchservice.team.model.Team;
import com.poorbet.matchservice.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final FileStorageService fileStorageService;

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

    @CacheEvict(value = "teams", key = "#id")
    @Transactional
    @Override
    public TeamShortDto updateLogo(UUID id, MultipartFile file) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        String imgPath = fileStorageService.store(id, file);
        team.setImg(imgPath);

        return TeamMapper.toTeamShortDto(teamRepository.save(team));
    }
}
