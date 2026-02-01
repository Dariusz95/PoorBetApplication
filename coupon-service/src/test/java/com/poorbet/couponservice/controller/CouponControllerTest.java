package com.poorbet.couponservice.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.poorbet.couponservice.domain.BetType;
import com.poorbet.couponservice.domain.Coupon;
import com.poorbet.couponservice.domain.CouponStatus;
import com.poorbet.couponservice.dto.CreateBetDto;
import com.poorbet.couponservice.dto.CreateCouponDto;
import com.poorbet.couponservice.service.CouponService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    private static final BigDecimal VALID_STAKE = new BigDecimal("50.00");
    private static final String COUPONS_ENDPOINT = "/api/coupons";

    private CreateCouponDto validCreateCouponDto;
    private Coupon couponResponse;

    @BeforeEach
    void setUp() {
        validCreateCouponDto = createValidCouponDto();
        couponResponse = Coupon.builder()
                .id(UUID.randomUUID())
                .stake(VALID_STAKE)
                .status(CouponStatus.OPEN)
                .build();
    }

    private CreateCouponDto createValidCouponDto() {
        CreateCouponDto dto = new CreateCouponDto();
        dto.setStake(VALID_STAKE);
        dto.setBets(Arrays.asList(
                createBetDto(BetType.HOME_WIN),
                createBetDto(BetType.DRAW)
        ));
        return dto;
    }

    private CreateBetDto createBetDto(BetType betType) {
        return CreateBetDto.builder()
                .matchId(UUID.randomUUID())
                .betType(betType)
                .build();
    }

    @Test
    @DisplayName("Should create coupon with valid request")
    void shouldReturn201CreatedStatus() throws Exception {
        // Arrange
        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(couponResponse);

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateCouponDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should validate stake is not null")
    void shouldValidateStakeIsNotNull() throws Exception {
        // Arrange
        CreateCouponDto invalidDto = new CreateCouponDto();
        invalidDto.setStake(null);
        invalidDto.setBets(Arrays.asList(createBetDto(BetType.HOME_WIN)));

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate stake minimum value")
    void shouldValidateStakeMinimumValue() throws Exception {
        // Arrange
        CreateCouponDto invalidDto = new CreateCouponDto();
        invalidDto.setStake(new BigDecimal("0.50"));
        invalidDto.setBets(Arrays.asList(createBetDto(BetType.HOME_WIN)));

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate bets are not empty")
    void shouldValidateBetsAreNotEmpty() throws Exception {
        // Arrange
        CreateCouponDto invalidDto = new CreateCouponDto();
        invalidDto.setStake(new BigDecimal("50.00"));
        invalidDto.setBets(Arrays.asList());

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate each bet has matchId")
    void shouldValidateEachBetHasMatchId() throws Exception {
        // Arrange
        CreateBetDto invalidBet = new CreateBetDto();
        invalidBet.setMatchId(null);
        invalidBet.setBetType(BetType.HOME_WIN);

        CreateCouponDto invalidDto = new CreateCouponDto();
        invalidDto.setStake(new BigDecimal("50.00"));
        invalidDto.setBets(Arrays.asList(invalidBet));

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should validate each bet has betType")
    void shouldValidateEachBetHasBetType() throws Exception {
        // Arrange
        CreateBetDto invalidBet = new CreateBetDto();
        invalidBet.setMatchId(UUID.randomUUID());
        invalidBet.setBetType(null);

        CreateCouponDto invalidDto = new CreateCouponDto();
        invalidDto.setStake(new BigDecimal("50.00"));
        invalidDto.setBets(Arrays.asList(invalidBet));

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept request with minimum valid stake")
    void shouldAcceptRequestWithMinimumValidStake() throws Exception {
        // Arrange
        CreateCouponDto minimumDto = new CreateCouponDto();
        minimumDto.setStake(new BigDecimal("1.00"));
        minimumDto.setBets(Arrays.asList(createBetDto(BetType.HOME_WIN)));

        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(couponResponse);

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimumDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept request with high stake value")
    void shouldAcceptRequestWithHighStakeValue() throws Exception {
        // Arrange
        CreateCouponDto highStakeDto = new CreateCouponDto();
        highStakeDto.setStake(new BigDecimal("10000.00"));
        highStakeDto.setBets(Arrays.asList(createBetDto(BetType.HOME_WIN)));

        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(couponResponse);

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(highStakeDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should call coupon service with provided DTO")
    void shouldCallCouponServiceWithProvidedDto() throws Exception {
        // Arrange
        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(couponResponse);

        // Act
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateCouponDto)))
                .andExpect(status().isCreated());

        // Assert
        verify(couponService, times(1)).createCoupon(
                any(CreateCouponDto.class),
                any(UUID.class)
        );
    }

    @Test
    @DisplayName("Should return created coupon in response body")
    void shouldReturnCreatedCouponInResponseBody() throws Exception {
        // Arrange
        UUID couponId = UUID.randomUUID();
        Coupon expectedCoupon = Coupon.builder()
                .id(couponId)
                .stake(new BigDecimal("50.00"))
                .status(CouponStatus.OPEN)
                .build();

        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(expectedCoupon);

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateCouponDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(couponId.toString())))
                .andExpect(jsonPath("$.stake", comparesEqualTo(50.00)))
                .andExpect(jsonPath("$.status", equalTo("OPEN")));
    }

    @Test
    @DisplayName("Should accept multiple bets in coupon")
    void shouldAcceptMultipleBetsInCoupon() throws Exception {
        // Arrange
        CreateCouponDto multipleBeetsDto = new CreateCouponDto();
        multipleBeetsDto.setStake(new BigDecimal("100.00"));
        multipleBeetsDto.setBets(Arrays.asList(
                createBetDto(BetType.HOME_WIN),
                createBetDto(BetType.DRAW),
                createBetDto(BetType.AWAY_WIN)
        ));

        when(couponService.createCoupon(any(CreateCouponDto.class), any(UUID.class)))
                .thenReturn(couponResponse);

        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(multipleBeetsDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should reject invalid JSON format")
    void shouldRejectInvalidJsonFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should require JSON content type")
    void shouldRequireJsonContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post(COUPONS_ENDPOINT)
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(validCreateCouponDto)))
                .andExpect(status().isUnsupportedMediaType());
    }
}
