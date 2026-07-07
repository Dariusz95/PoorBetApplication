package com.poorbet.matchservice.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("AfterCommitHandler Unit Tests")
class AfterCommitHandlerTest {

    private final AfterCommitHandler afterCommitHandler = new AfterCommitHandler();

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("Should run the action after the transaction commits")
    void shouldRunActionAfterCommit() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();
        Runnable action = mock(Runnable.class);

        // Act
        afterCommitHandler.run(action);
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(sync -> sync.afterCommit());

        // Assert
        verify(action).run();
    }

    @Test
    @DisplayName("Should not run the action if the transaction never commits")
    void shouldNotRunActionWithoutCommit() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();
        Runnable action = mock(Runnable.class);

        // Act
        afterCommitHandler.run(action);

        // Assert - no afterCommit() invoked
        verify(action, never()).run();
    }

    @Test
    @DisplayName("Should register exactly one synchronization per call")
    void shouldRegisterSynchronization() {
        // Arrange
        TransactionSynchronizationManager.initSynchronization();
        Runnable action = mock(Runnable.class);

        // Act
        afterCommitHandler.run(action);

        // Assert
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);
    }
}
