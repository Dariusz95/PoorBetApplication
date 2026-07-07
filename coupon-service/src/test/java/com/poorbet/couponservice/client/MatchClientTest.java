package com.poorbet.couponservice.client;

import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.dto.MatchBetSnapshotDto;
import com.poorbet.couponservice.dto.MatchResultMapDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchClient Unit Tests")
class MatchClientTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private MatchClient matchClient;

    private UUID matchId;

    private static final String HOME_TEAM = "Real Madrid";
    private static final String AWAY_TEAM = "Barcelona";
    public static final BigDecimal DEFAULT_ODD = new BigDecimal("1.50");

    public static MatchBetSnapshotDto createSnapshot(BigDecimal odd) {
        return new MatchBetSnapshotDto(
                UUID.randomUUID(),
                HOME_TEAM,
                AWAY_TEAM,
                OffsetDateTime.parse("2026-06-20T20:45:00Z"),
                odd
        );
    }

    public static MatchBetSnapshotDto createSnapshot() {
        return createSnapshot(DEFAULT_ODD);
    }

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        matchId = UUID.randomUUID();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupRestClientChainForGet(MatchBetSnapshotDto snapshotDto) {
        when(restClient.get()).thenReturn((RestClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(MatchBetSnapshotDto.class))
                .thenReturn(snapshotDto);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupRestClientChainForPost(MatchResultMapDto results) {
        when(restClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri("/internal/match/results"))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.body(any(Object.class)))
                .thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve())
                .thenReturn(responseSpec);

        when(responseSpec.body(MatchResultMapDto.class))
                .thenReturn(results);
    }

    @Test
    @DisplayName("Should get odd for match with correct URI")
    void shouldGetOddForMatchWithCorrectUri() {
        // Arrange
        BigDecimal expectedOdd = new BigDecimal("1.5");
        MatchBetSnapshotDto snapshot = createSnapshot(expectedOdd);
        setupRestClientChainForGet(snapshot);

        // Act
        MatchBetSnapshotDto result = matchClient.getBetSnapshot(matchId, BetType.HOME_WIN);

        // Assert
        assertThat(result.odd()).isEqualTo(expectedOdd);
    }

    @Test
    @DisplayName("Should call get request for odds endpoint")
    void shouldCallGetRequestForOddsEndpoint() {
        // Arrange
        MatchBetSnapshotDto snapshot = createSnapshot();
        setupRestClientChainForGet(snapshot);

        // Act
        matchClient.getBetSnapshot(matchId, BetType.HOME_WIN);

        // Assert
        verify(restClient).get();
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(MatchBetSnapshotDto.class);
    }

    @Test
    @DisplayName("Should block on getOdd response")
    void shouldBlockOnGetOddResponse() {
        // Arrange
        MatchBetSnapshotDto expectedSnapshot = createSnapshot();
        setupRestClientChainForGet(expectedSnapshot);

        // Act
        MatchBetSnapshotDto result = matchClient.getBetSnapshot(matchId, BetType.HOME_WIN);

        // Assert
        assertThat(result).isNotNull().isEqualTo(expectedSnapshot);
    }

    @Test
    @DisplayName("Should get match results for multiple match IDs")
    void shouldGetMatchResultsForMultipleMatchIds() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupRestClientChainForPost(expectedResults);

        // Act
        MatchResultMapDto result = matchClient.getMatchResult(matchIds);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should call post request for match results endpoint")
    void shouldCallPostRequestForMatchResultsEndpoint() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupRestClientChainForPost(expectedResults);

        // Act
        matchClient.getMatchResult(matchIds);

        // Assert
        verify(restClient).post();
    }

    @Test
    @DisplayName("Should send match IDs in request body")
    void shouldSendMatchIdsInRequestBody() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupRestClientChainForPost(expectedResults);

        // Act
        matchClient.getMatchResult(matchIds);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UUID>> bodyCaptor = ArgumentCaptor.forClass(List.class);
        verify(requestBodySpec).body(bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).isEqualTo(matchIds);
    }

    @Test
    @DisplayName("Should block on getMatchResult response")
    void shouldBlockOnGetMatchResultResponse() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupRestClientChainForPost(expectedResults);

        // Act
        MatchResultMapDto result = matchClient.getMatchResult(matchIds);

        // Assert
        assertThat(result).isNotNull();
    }
}
