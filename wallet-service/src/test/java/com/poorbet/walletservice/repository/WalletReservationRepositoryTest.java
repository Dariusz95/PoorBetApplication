package com.poorbet.walletservice.repository;

import com.poorbet.walletservice.domain.model.ReservationStatus;
import com.poorbet.walletservice.domain.model.WalletReservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression coverage for V5__fix_wallet_reservation_table.sql: previously the
 * entity mapped to `wallet_reservation` while V3 created `wallet_reservations`
 * with a CHECK constraint only allowing PENDING/CONFIRMED/CANCELLED, so with
 * ddl-auto=validate the service failed to start against a real database, and
 * persisting a RESERVED/COMMITTED/RELEASED reservation would have violated
 * the constraint.
 */
@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class WalletReservationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("wallet_reservation_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WalletReservationRepository walletReservationRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Test
    void save_shouldPersistReservation_withReservedStatus() {
        // Arrange
        WalletReservation reservation = WalletReservation.create(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00"));

        // Act
        walletReservationRepository.saveAndFlush(reservation);
        Optional<WalletReservation> found = walletReservationRepository.findById(reservation.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @Test
    void save_shouldPersistReservation_afterTransitioningThroughAllStatuses() {
        // Arrange
        WalletReservation reservation = WalletReservation.create(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00"));
        walletReservationRepository.saveAndFlush(reservation);

        // Act: RESERVED -> COMMITTED
        reservation.setStatus(ReservationStatus.COMMITTED);
        walletReservationRepository.saveAndFlush(reservation);
        WalletReservation committed = walletReservationRepository.findById(reservation.getId()).orElseThrow();
        assertThat(committed.getStatus()).isEqualTo(ReservationStatus.COMMITTED);

        // Act: COMMITTED -> RELEASED
        committed.setStatus(ReservationStatus.RELEASED);
        walletReservationRepository.saveAndFlush(committed);
        WalletReservation released = walletReservationRepository.findById(reservation.getId()).orElseThrow();

        // Assert
        assertThat(released.getStatus()).isEqualTo(ReservationStatus.RELEASED);
    }

    @Test
    void save_shouldRejectNonPositiveAmount() {
        // Arrange
        WalletReservation reservation = WalletReservation.create(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.ZERO);

        // Act & Assert
        org.junit.jupiter.api.Assertions.assertThrows(
                Exception.class,
                () -> walletReservationRepository.saveAndFlush(reservation));
    }
}
