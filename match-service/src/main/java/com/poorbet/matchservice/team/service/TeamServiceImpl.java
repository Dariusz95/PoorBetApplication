package com.poorbet.matchservice.team.service;

import com.poorbet.matchservice.team.client.wallet.WalletClient;
import com.poorbet.matchservice.team.dto.*;
import com.poorbet.matchservice.team.exception.TeamAlreadyExistsException;
import com.poorbet.matchservice.team.exception.TeamNotFoundException;
import com.poorbet.matchservice.team.mapper.TeamMapper;
import com.poorbet.matchservice.team.model.Team;
import com.poorbet.matchservice.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final FileStorageService fileStorageService;
    private final WalletClient walletClient;
    private final int DEFAULT_DEFENCE_POWER = 40;
    private final int DEFAULT_ATTACK_POWER = 40;
    //TODO moze jakas konfiguracja power 40-50 koszt 10, a 50-60 wiecej
    private static final BigDecimal POWER_INCREASE_COST = BigDecimal.valueOf(10);

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
        return teamRepository.findRandomTeams(count);
    }

    @Cacheable(value = "teams", key = "#id")
    @Transactional(readOnly = true)
    @Override
    public TeamShortDto getById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        return TeamMapper.toTeamShortDto(team);
    }

    @CacheEvict(value = "teams", key = "#result.id")
    @Transactional
    @Override
    public TeamResponse updateLogo(UUID userId, MultipartFile file) {
        Team team = teamRepository.findByUserId(userId)
                .orElseThrow(() -> new AccessDeniedException("No access"));

        String imgPath = fileStorageService.store(team.getId(), file);
        team.setImg(imgPath);

        return TeamMapper.toResponse(teamRepository.save(team));
    }

    @Override
    @Transactional
    public TeamResponse create(CreateTeamDto dto, UUID userId) {
        if (teamRepository.existsByUserId(userId)) {
            throw new TeamAlreadyExistsException();
        }

        Team team = Team.builder()
                .name(dto.name())
                .attackPower(DEFAULT_ATTACK_POWER)
                .defencePower(DEFAULT_DEFENCE_POWER)
                .userId(userId)
                .img(null)
                .build();

        Team saved = teamRepository.save(team);

        return TeamMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "teams", key = "#result.id")
    public TeamResponse update(UpdateTeamDto dto, UUID userId) {
        Team team = teamRepository.findByUserId(userId)
                .orElseThrow(() -> new TeamNotFoundException(userId));

        team.setName(dto.name());

        return TeamMapper.toResponse(team);
    }

    @Override
    @Transactional
    @CacheEvict(value = "teams", key = "#result.id")
    public TeamResponse increasePower(IncreaseTeamPowerDto dto, UUID userId) {
        Team team = teamRepository.findByUserId(userId)
                .orElseThrow(() -> new TeamNotFoundException(userId));

        walletClient.debit(userId, new DebitWalletRequest(POWER_INCREASE_COST));

        switch (dto.powerType()) {
            case ATTACK -> team.setAttackPower(team.getAttackPower() + 1);
            case DEFENCE -> team.setDefencePower(team.getDefencePower() + 1);
        }

        return TeamMapper.toResponse(team);
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = "teams", key = "#result.id")
    public TeamResponse getMyTeam(UUID userId) {
        Team team = teamRepository.findByUserId(userId)
                .orElseThrow(() -> new TeamNotFoundException(userId));

        return TeamMapper.toResponse(team);
    }
}
