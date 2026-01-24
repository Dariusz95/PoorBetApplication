package com.poorbet.matchservice.match.matchpool.service;

import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.match.domain.MatchStatus;
import com.poorbet.matchservice.match.matchpool.domain.PoolStatus;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;
import com.poorbet.matchservice.match.matchpool.mapper.MatchPoolMapper;
import com.poorbet.matchservice.match.matchpool.repository.MatchPoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchPoolServiceImpl implements MatchPoolService {
    private final MatchPoolRepository matchPoolRepository;
    private final MatchPoolMapper matchPoolMapper;
    private final MatchPoolSimulationService matchPoolSimulationService;

    @Transactional
    public void startPool(UUID poolId) {
        MatchPool pool = matchPoolRepository.findById(poolId)
                .orElseThrow();

        if (pool.getStatus() != PoolStatus.BETTABLE) return;

        pool.setStatus(PoolStatus.STARTED);
        pool.getMatches().forEach(m -> m.setStatus(MatchStatus.LIVE));

        matchPoolRepository.save(pool);

        matchPoolSimulationService.startPoolSimulation(pool.getId());
    }


    @Transactional(readOnly = true)
    public List<MatchPoolDto> getFutureMatchPools() {
        Pageable pageable = PageRequest.of(0, 3);
        List<MatchPool> matchPools = matchPoolRepository.getFutureMatchPools(pageable);

        return matchPoolMapper.toDto(matchPools);
    }
}
