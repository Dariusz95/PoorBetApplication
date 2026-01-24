package com.poorbet.matchservice.match.match.mapper;

import com.poorbet.matchservice.match.match.dto.MatchResultDto;
import com.poorbet.matchservice.match.match.dto.MatchResultMapDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MatchResultMapMapper {
    public MatchResultMapDto toDto(List<MatchResultDto> dtoList){
        Map<UUID, MatchResultDto> results = dtoList.stream()
                .collect(Collectors.toMap(
                        MatchResultDto::getId,
                        Function.identity()
                ));

        return MatchResultMapDto.builder()
                .results(results)
                .build();
    }
}
