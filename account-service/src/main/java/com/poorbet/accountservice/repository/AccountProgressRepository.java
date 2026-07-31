package com.poorbet.accountservice.repository;

import com.poorbet.accountservice.domain.model.AccountProgress;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountProgressRepository extends JpaRepository<AccountProgress, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM AccountProgress p WHERE p.userId = :userId")
    Optional<AccountProgress> findByUserIdForUpdate(@Param("userId") UUID userId);
}
