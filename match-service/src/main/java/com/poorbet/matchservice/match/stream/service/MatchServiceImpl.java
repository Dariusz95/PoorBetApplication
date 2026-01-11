package com.poorbet.matchservice.match.stream.service;

import com.poorbet.matchservice.match.stream.dto.response.MatchPoolDto;
import com.poorbet.matchservice.match.stream.mapper.MatchPoolMapper;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import com.poorbet.matchservice.match.stream.repository.MatchPoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService{
    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolMapper matchPoolMapper;

    @Transactional(readOnly = true)
    public List<MatchPoolDto> getFutureMatchPools(){
        Pageable pageable = PageRequest.of(0, 3);
        List<MatchPool> matchPools = matchPoolRepository.getFutureMatchPools(pageable);

        return matchPoolMapper.toDto(matchPools);
    }
}
