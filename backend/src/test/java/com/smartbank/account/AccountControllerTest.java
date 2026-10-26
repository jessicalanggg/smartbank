package com.smartbank.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean AccountService service;

    @Test
    void returnsAccountAsJson() throws Exception {
        UUID id = UUID.fromString("11111111-1111-4111-8111-111111111111");
        when(service.getAccount(id)).thenReturn(new AccountResponse(
            id, "DEMO-10001", "Demo Customer A", AccountType.CHECKING,
            new BigDecimal("1250.00"), "CAD"));
        mvc.perform(get("/api/v1/accounts/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountNumber").value("DEMO-10001"))
            .andExpect(jsonPath("$.balance").value(1250.00));
    }

    @Test
    void missingAccountReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getAccount(id)).thenThrow(new AccountNotFoundException(id));
        mvc.perform(get("/api/v1/accounts/{id}", id))
            .andExpect(status().isNotFound());
    }
}
