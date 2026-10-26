package com.smartbank.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        String ownerName,
        AccountType accountType,
        BigDecimal balance,
        String currency
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(), account.getAccountNumber(),
                account.getOwnerName(), account.getAccountType(),
                account.getBalance(), account.getCurrency());
    }
}
