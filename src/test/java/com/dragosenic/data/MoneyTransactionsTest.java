package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.common.MoneyDirection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MoneyTransactionsTest {

    @Test
    void moneyTransactionsTest() {

        String errorMessage = null;

        MoneyTransactions moneyTransactions = new MoneyTransactions();
        try {
            moneyTransactions.createMoneyTransaction(1, MoneyDirection.IN, new BigDecimal(1.23), 1234567890, "desc.");
            moneyTransactions.createMoneyTransaction(1, MoneyDirection.IN, new BigDecimal(3.21), 1234567890, "desc.");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        Assertions.assertNull(errorMessage);
        Assertions.assertEquals(new BigDecimal("4.44"), moneyTransactions.getBalance());
    }

}
