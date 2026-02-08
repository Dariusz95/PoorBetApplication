package com.poorbet.oddsservice.fixture;

import java.util.List;
import java.util.UUID;

import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.PredictOddsRequest;
import com.poorbet.oddsservice.dto.request.BatchPredictionRequest;
import com.poorbet.oddsservice.dto.request.MatchDto;

public class OddsFixtures {

    public static final UUID MATCH_1_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    public static final int MU_ATTACK = 78;
    public static final int MU_DEFENCE = 75;
    public static final int LIV_ATTACK = 82;
    public static final int LIV_DEFENCE = 80;


    public static PredictOddsRequest manchesterUnitedVsLiverpool() {
        return new PredictOddsRequest(MU_ATTACK, MU_DEFENCE, LIV_ATTACK, LIV_DEFENCE);
    }

    public static MatchDto manchesterUnitedVsLiverpoolMatch() {
        return new MatchDto(MATCH_1_ID, MU_ATTACK, MU_DEFENCE, LIV_ATTACK, LIV_DEFENCE);
    }

    public static OddsResponseDto homeAdvantageOdds() {
        return new OddsResponseDto(0.45f, 0.28f, 0.27f);
    }

    public static BatchPredictionRequest singleMatchBatch() {
        return new BatchPredictionRequest(List.of(manchesterUnitedVsLiverpoolMatch()));
    }

}
