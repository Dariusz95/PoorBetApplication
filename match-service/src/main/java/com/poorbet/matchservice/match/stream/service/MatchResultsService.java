package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.MatchResultDto;
import com.poorbet.matchservice.match.stream.dto.MatchResultMapDto;
import com.poorbet.matchservice.match.stream.mapper.MatchResultMapMapper;
import com.poorbet.matchservice.match.stream.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchResultsService {
    private final MatchResultMapMapper matchResultMapMapper;
    private final MatchRepository matchRepository;


    @Transactional(readOnly = true)
    public MatchResultMapDto getMatchResultMap(List<UUID> matchIds) {
        List<MatchResultDto> matches = matchRepository.findResultsByIds(matchIds);

        return matchResultMapMapper.toDto(matches);
    }
}
