package com.smartbank.transaction;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BankingController {
    private final BankingService service;
    private final com.smartbank.auth.AccessService access;

    public BankingController(BankingService service, com.smartbank.auth.AccessService access) {
        this.service = service;
        this.access = access;
    }

    @PostMapping("/accounts/{id}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        access.requireOwner(id);
        return service.deposit(id, request);
    }

    @PostMapping("/accounts/{id}/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        access.requireOwner(id);
        return service.withdraw(id, request);
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        access.requireOwner(request.fromAccountId());
        return service.transfer(request);
    }

    @PostMapping("/transfers/by-account-number")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transferByAccountNumber(
            @Valid @RequestBody TransferByAccountNumberRequest request) {

        access.requireOwner(request.fromAccountId());
        return service.transferByAccountNumber(request);
    }

    // Existing transaction history endpoint
    @GetMapping("/accounts/{id}/transactions")
    public List<TransactionResponse> history(@PathVariable UUID id) {
        access.requireOwner(id);
        return service.history(id);
    }
}
