package com.poorbet.accountservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_progress")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountProgress {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private long currentExp;

    @UpdateTimestamp
    private Instant updatedAt;

    public static AccountProgress createForUser(UUID userId) {
        AccountProgress progress = new AccountProgress();
        progress.userId = userId;
        progress.level = 1;
        progress.currentExp = 0;
        return progress;
    }
}
