package com.poorbet.oddsservice.service;

import com.poorbet.oddsservice.dto.OddsResponseDto;
import com.poorbet.oddsservice.dto.request.MatchDto;
import com.poorbet.oddsservice.dto.response.BatchOddsResponse;
import com.poorbet.oddsservice.model.OddsModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("OddsServiceImpl – unit tests")
@ExtendWith(MockitoExtension.class)
class OddsServiceImplTest {

    @Mock
    private OddsModel oddsModel;

    @InjectMocks
    private OddsServiceImpl oddsService;

    @Test
    void shouldDelegatePredictOddsToModel() {
        OddsResponseDto odds = new OddsResponseDto(0.5f, 0.3f, 0.2f);

        when(oddsModel.predict(70, 60, 65, 55))
                .thenReturn(odds);

        OddsResponseDto result = oddsService.predictOdds(70, 60, 65, 55);

        assertSame(odds, result);
        verify(oddsModel).predict(70, 60, 65, 55);
    }

    @Test
    void shouldReturnBatchWithSingleMatch() {
        UUID matchId = UUID.randomUUID();
        MatchDto match = new MatchDto(matchId, 70, 60, 65, 55);
        OddsResponseDto odds = new OddsResponseDto(0.4f, 0.3f, 0.3f);

        when(oddsModel.predict(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(odds);

        List<BatchOddsResponse> result =
                oddsService.predictBatch(List.of(match));

        assertEquals(1, result.size());
        assertEquals(matchId, result.get(0).matchId());
        assertSame(odds, result.get(0).oddsResponse());
    }

    @Test
    void shouldProcessMultipleMatches() {
        MatchDto m1 = new MatchDto(UUID.randomUUID(), 70, 60, 65, 55);
        MatchDto m2 = new MatchDto(UUID.randomUUID(), 80, 70, 60, 50);

        when(oddsModel.predict(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(new OddsResponseDto(0.5f, 0.3f, 0.2f));

        List<BatchOddsResponse> result =
                oddsService.predictBatch(List.of(m1, m2));

        assertEquals(2, result.size());
        verify(oddsModel, times(2))
                .predict(anyInt(), anyInt(), anyInt(), anyInt());
    }
}
