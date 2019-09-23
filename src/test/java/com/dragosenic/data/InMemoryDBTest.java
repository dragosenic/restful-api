package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.eBank.ElectronicBanking;
import com.dragosenic.eBank.ElectronicBankingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class InMemoryDBTest {

    private ElectronicBankingService eB;
    private int holderId1 = 0;
    private int holderId2 = 0;

    private int accountNumber1 = 0;
    private int accountNumber2 = 0;
    private int accountNumber3 = 0;

    /**
     *  Create two account holders and three accounts and deposit 1000 to first account
     */
    @BeforeEach
    void init() {

        eB = new ElectronicBanking(new InMemoryDB());
        String errorMessage1 = null;
        String errorMessage2 = null;
        String errorMessage3 = null;

        // 1. create two account holders
        try {
            holderId1 = eB.createNewAccountHolder("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
            holderId2 = eB.createNewAccountHolder("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
        } catch (InvalidPostDataException e) {
            errorMessage1 = e.getMessage();
        }

        // 2. create three accounts
        try {
            accountNumber1 = eB.createNewAccount("{\"type\": \"CHECKING\", \"accountHolder\": {\"id\": " + holderId1 + "}}");
            accountNumber2 = eB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + holderId1 + "}}");
            accountNumber3 = eB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + holderId2 + "}}");
        } catch (InvalidPostDataException e) {
            errorMessage2 = e.getMessage();
        }

        // 3. deposit money to account 1
        try {
            eB.createNewMoneyTransfer("{\"accountTo\": " + accountNumber1 + ", \"amount\": 1000, \"description\": \"initial deposit account 1\"}");
        } catch (InvalidPostDataException e) {
            errorMessage3 = e.getMessage();
        }

        // assert
        Assertions.assertEquals(
                new BigDecimal("1000.00"),
                eB.getAccounts().getAccountById(accountNumber1).getBalance());

        Assertions.assertNull(errorMessage1);
        Assertions.assertNull(errorMessage2);
        Assertions.assertNull(errorMessage3);
    }

    /**
     *  Transfer 7.77 from account 1 to account 2
     *
     *  expected:
     *      account1 to be 992.23 (i.e. 1000 - 7.77)
     *      account2 to be 7.77
     */
    @Test
    void moneyTransferTest1() {

        String errorMessage = null;

        try {
            eB.createNewMoneyTransfer("{\"accountFrom\": " + accountNumber1 + ", \"accountTo\": " + accountNumber2 + ", \"amount\": 7.77, \"description\":\"descr.\" }");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        // assert
        Assertions.assertEquals(
                new BigDecimal("992.23"),
                eB.getAccounts().getAccountById(accountNumber1).getBalance());

        Assertions.assertEquals(
                new BigDecimal("7.77"),
                eB.getAccounts().getAccountById(accountNumber2).getBalance());

        Assertions.assertNull(errorMessage);
    }

    /**
     *  Transfer 1000.01 from account 1 to account 2
     *  (i.e. should not happen because no sufficient funds on account 2)
     *
     *  expected:
     *      account1 to be 1000.01
     *      account2 to be 0
     */
    @Test
    void moneyTransferTest2() {

        String errorMessage = null;

        try {
            eB.createNewMoneyTransfer("{\"accountFrom\": " + accountNumber1 + ", \"accountTo\": " + accountNumber2 + ", \"amount\": 1000.01}");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        // assert
        Assertions.assertTrue(errorMessage != null && errorMessage.length() > 0);
        Assertions.assertEquals(
                new BigDecimal("1000.00"),
                eB.getAccounts().getAccountById(accountNumber1).getBalance());
        Assertions.assertEquals(
                new BigDecimal("0.00"),
                eB.getAccounts().getAccountById(accountNumber2).getBalance());
    }

    /**
     *  two consecutive money transfers
     *
     *  1. Transfer 7.77 from account 1 to account 2 (i.e. account1 = 992.23, account1 = 7.77)
     *  2. Transfer 10.00 from account 2 to account 3 (i.e. should not happen because no sufficient funds on account 2)
     *
     *  expected:
     *      account1 to be 992.23 (i.e. 1000 - 7.77)
     *      account2 to be 7.77
     *      account3 to be 0
     */
    @Test
    void moneyTransferTest3() {

        String errorMessage = null;

        try {
            eB.createNewMoneyTransfer("{\"accountFrom\": " + accountNumber1 + ", \"accountTo\": " + accountNumber2 + ", \"amount\": 7.77 }");
            eB.createNewMoneyTransfer("{\"accountFrom\": " + accountNumber2 + ", \"accountTo\": " + accountNumber3 + ", \"amount\": 10 }");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        // assert
        Assertions.assertTrue(errorMessage != null && errorMessage.length() > 0);
        Assertions.assertEquals(
                new BigDecimal("992.23"),
                eB.getAccounts().getAccountById(accountNumber1).getBalance());
        Assertions.assertEquals(
                new BigDecimal("7.77"),
                eB.getAccounts().getAccountById(accountNumber2).getBalance());
        Assertions.assertEquals(
                new BigDecimal("0.00"),
                eB.getAccounts().getAccountById(accountNumber3).getBalance());
    }

}
