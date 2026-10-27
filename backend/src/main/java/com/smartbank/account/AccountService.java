package com.smartbank.account;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID id) {
        return repository.findById(id)
                .map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listAccountsForUser(UUID userId) {
        return repository.findByOwnerUserIdOrderByAccountNumber(userId).stream()
                .map(AccountResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listAccounts() {
        return repository.findAll(Sort.by("accountNumber")).stream()
                .map(AccountResponse::from)
                .toList();
    }
}
