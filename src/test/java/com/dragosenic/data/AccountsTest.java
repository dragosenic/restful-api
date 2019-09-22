package com.dragosenic.data;

import com.dragosenic.common.AccountType;
import com.dragosenic.common.InvalidPostDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AccountsTest {

    @Test
    void accountCreateTest1() {

        AccountHolders accountHolders = new AccountHolders();

        int accountHolderId1 = 0;
        int accountHolderId2 = 0;
        String errorMessage1 = null;
        String errorMessage2 = null;

        try {
            accountHolderId1 = accountHolders.createNewAccountHolder("John Lennon","john@lennon.co.uk");
            accountHolderId2 = accountHolders.createNewAccountHolder("Paul McCartney","paul@mcca.co.uk");
        } catch (InvalidPostDataException e) {
            errorMessage1 = e.getMessage();
        }

        Accounts accounts = new Accounts(accountHolders);

        int accountId1 = 0;
        int accountId2 = 0;
        int accountId3 = 0;
        try {
            accountId1 = accounts.createAccount(accountHolderId1, AccountType.SAVING);
            accountId2 = accounts.createAccount(accountHolderId1, AccountType.CHECKING);
            accountId3 = accounts.createAccount(accountHolderId2, AccountType.CHECKING);
        } catch (InvalidPostDataException e) {
            errorMessage2 = e.getMessage();
        }

        Assertions.assertNull(errorMessage1);
        Assertions.assertNull(errorMessage2);
        Assertions.assertEquals(AccountType.SAVING, accounts.getAccountById(accountId1).getType());
        Assertions.assertEquals(AccountType.CHECKING, accounts.getAccountById(accountId2).getType());
        Assertions.assertEquals(AccountType.CHECKING, accounts.getAccountById(accountId3).getType());

        Assertions.assertEquals(accountHolderId1, accounts.getAccountById(accountId1).getAccountHolder().getId());
        Assertions.assertEquals(accountHolderId1, accounts.getAccountById(accountId2).getAccountHolder().getId());
        Assertions.assertEquals(accountHolderId2, accounts.getAccountById(accountId3).getAccountHolder().getId());
    }
}
