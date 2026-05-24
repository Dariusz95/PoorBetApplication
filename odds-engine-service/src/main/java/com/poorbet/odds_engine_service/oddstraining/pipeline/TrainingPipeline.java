package com.poorbet.odds_engine_service.oddstraining.pipeline;

import com.poorbet.odds_engine_service.oddstraining.dataset.DatasetWriter;
import com.poorbet.odds_engine_service.oddstraining.generator.MatchGenerator;
import com.poorbet.odds_engine_service.oddstraining.model.MatchRecord;
import com.poorbet.odds_engine_service.simulation.request.SimulationBatchRequest;
import com.poorbet.odds_engine_service.simulation.request.SimulationRequestDto;
import com.poorbet.odds_engine_service.simulation.service.MatchSimulationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingPipeline {

    private final MatchGenerator matchGenerator;
    private final DatasetWriter datasetWriter;
    private final MatchSimulationServiceImpl matchSimulationService;

    public void run() {
        List<SimulationRequestDto> matches = matchGenerator.getMatches();

        Map<UUID, SimulationRequestDto> uuidSimulationRequestMap =
                matches.stream()
                        .collect(Collectors.toMap(
                                SimulationRequestDto::matchId,
                                Function.identity()
                        ));

        SimulationBatchRequest request = new SimulationBatchRequest(matches);

        List<MatchRecord> results = matchSimulationService.simulateBatch(request).stream()
                .map(result -> {
                    log.info("result {}", result);
                    SimulationRequestDto data = uuidSimulationRequestMap.get(result.matchId());

                    return new MatchRecord(
                            data.home().attackPower(),
                            data.home().defencePower(),
                            data.away().attackPower(),
                            data.away().defencePower(),
                            getResult(result.homeGoals(), result.awayGoals())
                    );
                })
                .toList();

        datasetWriter.write(results);
    }

    private String getResult(int homeGoals, int awayGoals) {
        if (homeGoals == awayGoals) return "X";

        if (homeGoals > awayGoals) {
            return "H";
        } else {
            return "A";
        }
    }
}
