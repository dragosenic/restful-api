package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.model.AccountHolder;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class AccountHoldersTest {

    @Test
    void accountHolderCreateTest1() {

        AccountHolders accountHolders = new AccountHolders();

        int accountHolderId = 0;
        String errorMessage = null;

        try {
            accountHolderId = accountHolders.createNewAccountHolder("John Lennon","john@lennon.co.uk");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        AccountHolder accountHolder = accountHolders.getAccountHolderById(accountHolderId);

        Assertions.assertNotNull(accountHolder);
        Assertions.assertEquals("John Lennon", accountHolder.getFullName());
        Assertions.assertEquals("john@lennon.co.uk", accountHolder.getEmailPhoneAddress());
        Assertions.assertEquals(accountHolderId, accountHolder.getId());
        Assertions.assertNull(errorMessage);
    }

    @Test
    void accountHolderCreateTest2() {

        AccountHolders accountHolders = new AccountHolders();

        int accountHolderId = 0;
        String errorMessage = null;

        try {
            accountHolderId = accountHolders.createNewAccountHolder("","john@lennon.co.uk");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        Assertions.assertTrue(errorMessage != null && errorMessage.length() > 0);
        Assertions.assertNull(accountHolders.getAccountHolderById(accountHolderId));
    }

    @Test
    void accountHolderCreateTest3() {

        AccountHolders accountHolders = new AccountHolders();
        String errorMessage = null;

        try {
            accountHolders.createNewAccountHolder("John Lennon","john@lennon.co.uk");
            accountHolders.createNewAccountHolder("Paul McCartney","paul@mcca.co.uk");
            accountHolders.createNewAccountHolder("George Harrison","george@harrison.co.uk");
            accountHolders.createNewAccountHolder("Ringo Starr","ringo@starr.co.uk");
        } catch (InvalidPostDataException e) {
            errorMessage = e.getMessage();
        }

        ArrayList deserialized = new Gson().fromJson(accountHolders.getAllAccountHoldersInJsonFormat(), ArrayList.class);

        Assertions.assertEquals(4, deserialized.size());
        Assertions.assertNull(errorMessage);
    }

}
