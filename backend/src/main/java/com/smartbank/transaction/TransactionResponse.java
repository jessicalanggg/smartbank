package com.smartbank.transaction;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record TransactionResponse(UUID id, TransactionType type, UUID fromAccountId,
    UUID toAccountId, BigDecimal amount, String currency, Instant createdAt) {
    public static TransactionResponse from(BankingTransaction tx) {
        return new TransactionResponse(tx.getId(), tx.getTransactionType(), tx.getSourceAccountId(),
            tx.getDestinationAccountId(), tx.getAmount(), tx.getCurrency(), tx.getCreatedAt());
    }
}
