package com.smartbank.account;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {
    private final AccountRepository repository = mock(AccountRepository.class);
    private final AccountService service = new AccountService(repository);

    @Test
    void missingAccountThrowsNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> service.getAccount(id));
        verify(repository).findById(id);
    }
}
