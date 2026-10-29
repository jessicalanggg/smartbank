package com.smartbank.account;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountNumber(String accountNumber);

    java.util.List<Account> findByOwnerUserIdOrderByAccountNumber(UUID ownerUserId);

    boolean existsByIdAndOwnerUserId(UUID id, UUID ownerUserId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("update Account a set a.ownerUserId = :userId where a.id = :id and a.ownerUserId is null")
    int assignOwnerIfUnassigned(@Param("id") UUID id, @Param("userId") UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findLockedById(@Param("id") UUID id);
}
