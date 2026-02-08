package com.poorbet.couponservice.client;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.poorbet.couponservice.domain.OddsType;
import com.poorbet.couponservice.dto.MatchResultMapDto;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchClient Unit Tests")
class MatchClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MatchClient matchClient;

    private UUID matchId;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        matchId = UUID.randomUUID();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupWebClientChainForGet(Double oddValue) {
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Double.class))
                .thenReturn(Mono.just(oddValue));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setupWebClientChainForPost(MatchResultMapDto results) {
        when(webClient.post()).thenReturn((WebClient.RequestBodyUriSpec) requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/api/match/results")).thenReturn((WebClient.RequestBodySpec) requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(MatchResultMapDto.class))
                .thenReturn(Mono.just(results));
    }

    @Test
    @DisplayName("Should get odd for match with correct URI")
    void shouldGetOddForMatchWithCorrectUri() {
        // Arrange
        Double expectedOdd = 1.5;
        setupWebClientChainForGet(expectedOdd);

        // Act
        Double result = matchClient.getOdd(matchId, OddsType.HOME_WIN);

        // Assert
        assertThat(result).isEqualTo(expectedOdd);
    }

    @Test
    @DisplayName("Should call get request for odds endpoint")
    void shouldCallGetRequestForOddsEndpoint() {
        // Arrange
        setupWebClientChainForGet(1.5);

        // Act
        matchClient.getOdd(matchId, OddsType.HOME_WIN);

        // Assert
        verify(webClient).get();
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(Double.class);
    }

    @Test
    @DisplayName("Should block on getOdd response")
    void shouldBlockOnGetOddResponse() {
        // Arrange
        Double expectedOdd = 3.0;
        setupWebClientChainForGet(expectedOdd);

        // Act
        Double result = matchClient.getOdd(matchId, OddsType.HOME_WIN);

        // Assert
        assertThat(result).isNotNull().isEqualTo(expectedOdd);
    }

    @Test
    @DisplayName("Should get match results for multiple match IDs")
    void shouldGetMatchResultsForMultipleMatchIds() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupWebClientChainForPost(expectedResults);

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
        setupWebClientChainForPost(expectedResults);

        // Act
        matchClient.getMatchResult(matchIds);

        // Assert
        verify(webClient).post();
        verify(requestHeadersSpec).retrieve();
    }

    @Test
    @DisplayName("Should send match IDs in request body")
    void shouldSendMatchIdsInRequestBody() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupWebClientChainForPost(expectedResults);

        // Act
        matchClient.getMatchResult(matchIds);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UUID>> bodyCaptor = ArgumentCaptor.forClass(List.class);
        verify(requestBodySpec).bodyValue(bodyCaptor.capture());
        assertThat(bodyCaptor.getValue()).isEqualTo(matchIds);
    }

    @Test
    @DisplayName("Should block on getMatchResult response")
    void shouldBlockOnGetMatchResultResponse() {
        // Arrange
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        MatchResultMapDto expectedResults = MatchResultMapDto.builder().build();
        setupWebClientChainForPost(expectedResults);

        // Act
        MatchResultMapDto result = matchClient.getMatchResult(matchIds);

        // Assert
        assertThat(result).isNotNull();
    }
}
