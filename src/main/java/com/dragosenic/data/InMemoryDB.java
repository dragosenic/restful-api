package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.common.MoneyDirection;
import com.dragosenic.model.Account;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.model.TransferRequest;
import com.google.gson.Gson;

import java.math.BigDecimal;

public class InMemoryDB {

    private AccountHolders accountHolders;
    private Accounts accounts;

    public InMemoryDB() {
        accountHolders = new AccountHolders();
        accounts = new Accounts(accountHolders);
    }

    public AccountHolders getAccountHolders() {
        return accountHolders;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public int createNewAccount(String jsonData) throws InvalidPostDataException {

        Account account = null;
        try {
            account = new Gson().fromJson(jsonData, Account.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        int newAccountNumber = 0;
        try {
            newAccountNumber = accounts.createAccount(
                    account.getAccountHolder().getId(),
                    account.getType());
        } catch (Exception e) {
            throw new InvalidPostDataException("Create account error:" + e.getMessage());
        }

        return newAccountNumber;
    }

    public int createNewAccountHolder(String jsonData) throws InvalidPostDataException {

        AccountHolder accountHolder = null;
        try {
            accountHolder = new Gson().fromJson(jsonData, AccountHolder.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        int accountHolderId = 0;
        try {
            accountHolderId = accountHolders.createNewAccountHolder(
                    accountHolder.getFullName(),
                    accountHolder.getEmailPhoneAddress());
        } catch (Exception e) {
            throw new InvalidPostDataException("Create AccountHolder error:" + e.getMessage());
        }

        return accountHolderId;
    }

    public void createNewMoneyTransfer(String jsonData) throws InvalidPostDataException {

        TransferRequest transferRequest = null;
        try {
            transferRequest = new Gson().fromJson(jsonData, TransferRequest.class);
        } catch (Exception e) {
            throw new InvalidPostDataException("Invalid POST data: " + e.getMessage());
        }

        Account toAccount = accounts.getAccountById(transferRequest.getAccountTo());
        if (toAccount == null) {
            throw new InvalidPostDataException("'To' Account not found");
        }

        BigDecimal amount = new BigDecimal(transferRequest.getAmount());
        if (amount.compareTo(BigDecimal.ZERO) != 1) {
            throw new InvalidPostDataException("Amount to transfer is not provided or not valid");
        }

        try {

            Account fromAccount = accounts.getAccountById(transferRequest.getAccountFrom());

            if (fromAccount != null) {
                this.transferMoney(
                        fromAccount,
                        toAccount,
                        new BigDecimal(transferRequest.getAmount()),
                        transferRequest.getDescription());
            } else {
                this.depositMoney(
                        toAccount,
                        new BigDecimal(transferRequest.getAmount()),
                        transferRequest.getDescription());
            }

        } catch (Exception e) {
            throw new InvalidPostDataException("Transfer money error:" + e.getMessage());
        }

    }

    private void transferMoney (Account fromAccount, Account toAccount, BigDecimal amount, String description) throws InvalidPostDataException {

        long timestamp = System.currentTimeMillis();

        if (fromAccount.getBalance().compareTo(amount) == -1) {
            throw new InvalidPostDataException("No sufficient fund to complete transaction");
        }

        fromAccount.getMoneyTransactions()
                .createMoneyTransaction(timestamp, MoneyDirection.OUT, amount, toAccount.getAccountNumber(), description);
        toAccount.getMoneyTransactions()
                .createMoneyTransaction(timestamp, MoneyDirection.IN, amount, fromAccount.getAccountNumber(), description);
    }

    private void depositMoney (Account account, BigDecimal amount, String description) throws InvalidPostDataException {

        long timestamp = System.currentTimeMillis();

        account.getMoneyTransactions()
                .createMoneyTransaction(timestamp, MoneyDirection.IN, amount, account.getAccountNumber(), description);
    }

}
