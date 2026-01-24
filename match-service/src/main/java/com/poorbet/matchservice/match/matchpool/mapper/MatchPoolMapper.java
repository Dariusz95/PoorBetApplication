package com.poorbet.matchservice.match.matchpool.mapper;

import com.poorbet.matchservice.match.match.dto.response.MatchDto;
import com.poorbet.matchservice.match.matchpool.dto.MatchPoolDto;
import com.poorbet.matchservice.match.match.dto.response.OddsDto;
import com.poorbet.matchservice.match.match.domain.Match;
import com.poorbet.matchservice.match.matchpool.domain.MatchPool;
import com.poorbet.matchservice.match.match.domain.Odds;
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
        return new MatchDto(match.getId(), match.getHomeTeamId(), match.getAwayTeamId(), toOddsDto(match.getOdds()));
    }

    private OddsDto toOddsDto(Odds odds){
        return new OddsDto(odds.getId(), odds.getHomeWin(), odds.getDraw(), odds.getAwayWin());
    }
}
