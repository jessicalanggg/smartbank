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
    public BankingController(BankingService service) { this.service = service; }

    @PostMapping("/accounts/{id}/deposits")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse deposit(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        return service.deposit(id, request);
    }
    @PostMapping("/accounts/{id}/withdrawals")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse withdraw(@PathVariable UUID id, @Valid @RequestBody MoneyRequest request) {
        return service.withdraw(id, request);
    }
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        return service.transfer(request);
    }
    @GetMapping("/accounts/{id}/transactions")
    public List<TransactionResponse> history(@PathVariable UUID id) {
        return service.history(id);
    }
}
