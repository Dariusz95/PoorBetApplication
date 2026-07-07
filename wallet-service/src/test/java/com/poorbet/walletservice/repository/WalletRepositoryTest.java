package com.poorbet.walletservice.repository;

import com.poorbet.walletservice.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
class WalletRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("wallet_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private WalletRepository walletRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    private UUID userId;

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        userId = UUID.randomUUID();
        walletRepository.save(Wallet.builder()
                .userId(userId)
                .balance(new BigDecimal("100.00"))
                .build());
    }

    @Test
    void findByUserId_shouldReturnWallet_whenItExists() {
        // Act
        Optional<Wallet> result = walletRepository.findByUserId(userId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenWalletDoesNotExist() {
        // Act
        Optional<Wallet> result = walletRepository.findByUserId(UUID.randomUUID());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByUserId_shouldReturnTrue_whenWalletExists() {
        // Act & Assert
        assertThat(walletRepository.existsByUserId(userId)).isTrue();
    }

    @Test
    void existsByUserId_shouldReturnFalse_whenWalletDoesNotExist() {
        // Act & Assert
        assertThat(walletRepository.existsByUserId(UUID.randomUUID())).isFalse();
    }

    @Test
    void findByUserIdForUpdate_shouldReturnWallet_lockedForUpdate() {
        // Act
        Optional<Wallet> result = walletRepository.findByUserIdForUpdate(userId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }

    @Test
    void save_shouldEnforceUniqueUserIdConstraint() {
        // Arrange
        Wallet duplicate = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.TEN)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> walletRepository.saveAndFlush(duplicate))
                .isInstanceOf(Exception.class);
    }
}
