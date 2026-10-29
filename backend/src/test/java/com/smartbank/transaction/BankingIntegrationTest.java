package com.smartbank.transaction;

import com.smartbank.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")

@Testcontainers
@SpringBootTest
class BankingIntegrationTest {
    private static final UUID A = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final UUID B = UUID.fromString("22222222-2222-4222-8222-222222222222");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    BankingService banking;
    @Autowired
    AccountRepository accounts;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void resetDatabase() {
        // Each test starts with Flyway's seeded balances and no transaction records.
        jdbc.update("DELETE FROM banking_transactions");
        jdbc.update("UPDATE accounts SET balance = 1250.00, version = version + 1 WHERE id = ?", A);
        jdbc.update("UPDATE accounts SET balance = 3800.50, version = version + 1 WHERE id = ?", B);
    }

    private BigDecimal balance(UUID id) {
        return accounts.findById(id).orElseThrow().getBalance();
    }

    private void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    private long transactionCount() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM banking_transactions", Long.class);
    }

    @Test
    void successfulTransferUpdatesBothBalancesAndPersistsTransaction() {
        TransactionResponse response = banking.transfer(
                new TransferRequest(A, B, new BigDecimal("200.00"), "it-success"));

        assertNotNull(response.id());
        assertMoney("1050.00", balance(A));
        assertMoney("4000.50", balance(B));
        assertEquals(1L, transactionCount());
        assertEquals("TRANSFER", jdbc.queryForObject(
                "SELECT transaction_type FROM banking_transactions WHERE id = ?", String.class, response.id()));
    }

    @Test
    void insufficientFundsDoesNotChangeBalancesOrWriteTransaction() {
        assertThrows(BankingException.class, () -> banking.transfer(
                new TransferRequest(A, B, new BigDecimal("2000.00"), "it-insufficient")));

        assertMoney("1250.00", balance(A));
        assertMoney("3800.50", balance(B));
        assertEquals(0L, transactionCount());
    }

    @Test
    void repeatedRequestKeyReturnsSameTransactionWithoutSecondDebit() {
        TransferRequest request = new TransferRequest(A, B, new BigDecimal("75.00"), "it-repeat");
        TransactionResponse first = banking.transfer(request);
        TransactionResponse second = banking.transfer(request);

        assertEquals(first.id(), second.id());
        assertMoney("1175.00", balance(A));
        assertMoney("3875.50", balance(B));
        assertEquals(1L, transactionCount());
    }

    @Test
    void conflictingReuseOfRequestKeyIsRejected() {
        banking.transfer(new TransferRequest(A, B, new BigDecimal("50.00"), "it-conflict"));
        assertThrows(BankingException.class, () -> banking.transfer(
                new TransferRequest(A, B, new BigDecimal("60.00"), "it-conflict")));

        assertMoney("1200.00", balance(A));
        assertMoney("3850.50", balance(B));
        assertEquals(1L, transactionCount());
    }

    @Test
    void sameAccountTransferIsRejected() {
        assertThrows(BankingException.class, () -> banking.transfer(
                new TransferRequest(A, A, new BigDecimal("10.00"), "it-same")));
        assertMoney("1250.00", balance(A));
        assertEquals(0L, transactionCount());
    }

    @Test
    void transferByAccountNumberWorks() {
        TransactionResponse response = banking.transferByAccountNumber(
                new TransferByAccountNumberRequest(A, " DEMO-10002 ", new BigDecimal("25.00"), "it-number"));

        assertNotNull(response.id());
        assertMoney("1225.00", balance(A));
        assertMoney("3825.50", balance(B));
        assertEquals(1L, transactionCount());
    }

    @Test
    void crossCurrencyTransferIsRejectedWithoutChangingBalances() {
        jdbc.update("UPDATE accounts SET currency = 'USD' WHERE id = ?", B);
        try {
            assertThrows(BankingException.class, () -> banking.transfer(
                    new TransferRequest(A, B, new BigDecimal("10.00"), "it-currency")));
            assertMoney("1250.00", balance(A));
            assertMoney("3800.50", balance(B));
            assertEquals(0L, transactionCount());
        } finally {
            jdbc.update("UPDATE accounts SET currency = 'CAD' WHERE id = ?", B);
        }
    }

    @Test
    void databaseConstraintFailureRollsBackBothBalances() {
        String invalidKey = "x".repeat(101);

        assertThrows(RuntimeException.class, () -> banking.transfer(
                new TransferRequest(
                        A,
                        B,
                        new BigDecimal("100.00"),
                        invalidKey)));

        assertMoney("1250.00", balance(A));
        assertMoney("3800.50", balance(B));
        assertEquals(0L, transactionCount());
    }
}