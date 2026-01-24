package com.poorbet.couponservice.service;

import com.poorbet.couponservice.client.MatchClient;
import com.poorbet.couponservice.dto.MatchResultMapDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BetService {
    private final MatchClient matchClient;

    public MatchResultMapDto getResults(List<UUID> matchIds) {
        return matchClient.getMatchResult(matchIds);
    }
}
