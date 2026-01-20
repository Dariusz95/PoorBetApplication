package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.MatchResultDto;
import com.poorbet.matchservice.match.stream.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.stream.mapper.MatchResultMapMapper;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchResultsService {
    private final RedisTemplate<String, MatchResultDto> redisTemplate;
    private final MatchResultMapMapper matchResultMapMapper;
    private final MatchRepository repository;


    @Transactional(readOnly = true)
    public MatchResultMapDto getMatchResultMap(List<UUID> matchIds) {
        List<MatchResultDto> result = new ArrayList<>();
        List<UUID> missingIds = new ArrayList<>();

        for (UUID id : matchIds) {
            MatchResultDto cached = redisTemplate.opsForValue().get("match:result:" + id);
            if (cached != null) {
                result.add(cached);
            } else {
                missingIds.add(id);
            }
        }

        if (!missingIds.isEmpty()) {
            List<Match> matches = repository.findAllById(missingIds);

            List<MatchResultDto> missingResults = matches.stream()
                    .map(this::toDto)
                    .peek(dto -> redisTemplate.opsForValue()
                            .set("match:result:" + dto.getId(), dto, Duration.ofMinutes(10)))
                    .toList();

            result.addAll(missingResults);
        }

        return matchResultMapMapper.toDto(result);
    }

    private MatchResultDto toDto(Match match) {
        return MatchResultDto.builder()
                .id(match.getId())
                .homeScore(match.getHomeGoals())
                .awayScore(match.getAwayGoals())
                .matchStatusDto(match.getStatus())
                .build();
    }
}
