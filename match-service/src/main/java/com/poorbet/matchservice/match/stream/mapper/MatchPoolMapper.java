package com.poorbet.matchservice.match.stream.mapper;

import com.poorbet.matchservice.match.stream.dto.response.MatchDto;
import com.poorbet.matchservice.match.stream.dto.response.MatchPoolDto;
import com.poorbet.matchservice.match.stream.model.Match;
import com.poorbet.matchservice.match.stream.model.MatchPool;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MatchPoolMapper {
    public List<MatchPoolDto> toDto(List<MatchPool> pools){
        return pools.stream()
                .map(pool -> {
                    List<MatchDto> matchDtos = pool.getMatches().stream()
                            .map(this::toMatchDto)
                            .toList();

                    return new MatchPoolDto(
                            pool.getId(),
                            pool.getStatus(),
                            pool.getScheduledStartTime(),
                            matchDtos
                    );
                })
                .toList();
    }

    private MatchDto toMatchDto(Match match){
        return new MatchDto(match.getMatchId(), match.getHomeTeamId(), match.getAwayTeamId());
    }
}
