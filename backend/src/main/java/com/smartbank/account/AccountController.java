package com.smartbank.account;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService service;
    private final com.smartbank.auth.AccessService access;

    public AccountController(AccountService service, com.smartbank.auth.AccessService access) {
        this.service = service; this.access = access;
    }

    @GetMapping
    public List<AccountResponse> list() {
        return service.listAccountsForUser(access.currentUserId());
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable UUID id) {
        access.requireOwner(id);
        return service.getAccount(id);
    }
}
