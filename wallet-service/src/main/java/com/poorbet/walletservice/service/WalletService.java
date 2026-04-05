package com.poorbet.walletservice.service;

import com.poorbet.commons.rabbit.events.wallet.WalletCreatedEvent;
import com.poorbet.commons.rabbit.events.wallet.WalletEvents;
import com.poorbet.walletservice.domain.Wallet;
import com.poorbet.walletservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final OutboxService outboxService;


    @Transactional
    public void handleUserCreated(UUID userId) {
        try {
            log.info("userId = {}", userId);
            Wallet wallet = Wallet.builder()
                    .userId(userId)
                    .balance(BigDecimal.ZERO)
                    .build();

            walletRepository.save(wallet);

            outboxService.saveEvent(
                    WalletEvents.WALLET_CREATED,
                    new WalletCreatedEvent(userId)
            );

        } catch (DataIntegrityViolationException e) {
            log.info("exists = {}", e.getMessage());
        }
    }
}
