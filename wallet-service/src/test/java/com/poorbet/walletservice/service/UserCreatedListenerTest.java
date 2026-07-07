package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.EventEnvelope;
import com.poorbet.commons.rabbit.events.auth.UserCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCreatedListener Unit Tests")
class UserCreatedListenerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserCreatedListener userCreatedListener;

    @Test
    @DisplayName("Should delegate user id from user-created payload to WalletService")
    void shouldDelegateToWalletService() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserCreatedEvent payload = new UserCreatedEvent(userId);
        EventEnvelope<UserCreatedEvent> envelope = new EventEnvelope<>("USER_CREATED", "v1", "auth-service", payload);

        // Act
        userCreatedListener.handleUserCreated(envelope);

        // Assert
        verify(walletService).handleUserCreated(userId);
    }
}
