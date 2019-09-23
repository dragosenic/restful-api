package com.dragosenic.eBank;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.data.AccountHolders;
import com.dragosenic.data.Accounts;
import com.dragosenic.model.Account;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.model.TransferRequest;
import com.google.gson.Gson;

import java.math.BigDecimal;

public class ElectronicBanking implements ElectronicBankingService {

    private DatabaseService db;

    public ElectronicBanking(DatabaseService db) {
        this.db = db;
    }

    public AccountHolders getAccountHolders() {
        return this.db.getAccountHolders();
    }

    public Accounts getAccounts() {
        return this.db.getAccounts();
    }

    public int createNewAccount(String jsonData) throws InvalidPostDataException {

        Account account = null;
        try {
            account = new Gson().fromJson(jsonData, Account.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        return this.db.createNewAccount(account);
    }

    public int createNewAccountHolder(String jsonData) throws InvalidPostDataException {

        AccountHolder accountHolder = null;
        try {
            accountHolder = new Gson().fromJson(jsonData, AccountHolder.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        return this.db.createNewAccountHolder(accountHolder);
    }

    public void createNewMoneyTransfer(String jsonData) throws InvalidPostDataException {

        TransferRequest transferRequest = null;
        try {
            transferRequest = new Gson().fromJson(jsonData, TransferRequest.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        BigDecimal amount = new BigDecimal(transferRequest.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) != 1) {
            throw new InvalidPostDataException("Amount to transfer is not provided or not valid");
        }

        this.db.createNewMoneyTransfer(transferRequest);
    }
}
