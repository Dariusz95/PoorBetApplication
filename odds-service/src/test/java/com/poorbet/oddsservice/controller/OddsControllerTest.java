package com.poorbet.oddsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.PredictOddsRequest;
import com.poorbet.oddsservice.dto.request.BatchPredictionRequest;
import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.fixture.OddsFixtures;
import com.poorbet.oddsservice.service.OddsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OddsController.class)
@DisplayName("OddsController – WebMvcTest")
class OddsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OddsService oddsService;

    @Test
    void shouldPredictOddsSuccessfully() throws Exception {
        PredictOddsRequest request = OddsFixtures.manchesterUnitedVsLiverpool();
        OddsResponseDto response = OddsFixtures.homeAdvantageOdds();

        when(oddsService.predictOdds(
                request.homeTeamAttack(),
                request.homeTeamDefense(),
                request.awayTeamAttack(),
                request.awayTeamDefense()
        )).thenReturn(response);

        mockMvc.perform(post("/api/odds/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeWinProbability").value(0.45f))
                .andExpect(jsonPath("$.drawProbability").value(0.28f))
                .andExpect(jsonPath("$.awayWinProbability").value(0.27f));

        verify(oddsService).predictOdds(
                request.homeTeamAttack(),
                request.homeTeamDefense(),
                request.awayTeamAttack(),
                request.awayTeamDefense()
        );
    }

    @Test
    void shouldRejectInvalidPredictRequest() throws Exception {
        PredictOddsRequest invalid = new PredictOddsRequest(-1, 50, 60, 70);

        mockMvc.perform(post("/api/odds/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldPredictBatchOddsSuccessfully() throws Exception {
        BatchPredictionRequest request = OddsFixtures.singleMatchBatch();
        List<BatchOddsResponse> responses = List.of(
                new BatchOddsResponse(
                        OddsFixtures.MATCH_1_ID,
                        OddsFixtures.homeAdvantageOdds()
                )
        );

        when(oddsService.predictBatch(request.matches()))
                .thenReturn(responses);

        mockMvc.perform(post("/api/odds/predict/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictions", hasSize(1)))
                .andExpect(jsonPath("$.predictions[0].matchId")
                        .value(OddsFixtures.MATCH_1_ID.toString()));

        verify(oddsService).predictBatch(request.matches());
    }

    @Test
    void shouldRejectEmptyBatchRequest() throws Exception {
        BatchPredictionRequest request =
                new BatchPredictionRequest(List.of());

        mockMvc.perform(post("/api/odds/predict/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
