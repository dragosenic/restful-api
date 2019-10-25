package com.dragosenic.data;

import com.dragosenic.common.AccountType;
import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.model.Account;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.utilities.RND;
import com.google.gson.Gson;

import java.util.HashMap;

public class Accounts {

    private AccountHolders accountHolders;
    private HashMap<Integer, Account> accounts;

    Accounts(AccountHolders accountHolders) {
        this.accountHolders = accountHolders;
        accounts = new HashMap<>();
    }

    int createAccount(int accountHolderId, AccountType accountType) throws InvalidPostDataException {

        if (accountHolderId <= 0) {
            throw new InvalidPostDataException("Account holder id is not provided");
        }

        AccountHolder accountHolder;
        if ((accountHolder = this.accountHolders.getAccountHolderById(accountHolderId)) == null) {
            throw new InvalidPostDataException("Account holder not found");
        }

        if (accountType == null) {
            throw new InvalidPostDataException("Account type is not provided or not valid");
        }

        int newAccountNumber;
        do {
            newAccountNumber = RND.generateNewAccountNumber();
        } while (accounts.containsKey(newAccountNumber));

        Account account = new Account(newAccountNumber, accountHolder);
        account.setType(accountType);

        accounts.put(account.getAccountNumber(), account);

        return account.getAccountNumber();
    }

    public Account getAccountById(int accountNumber) {
        if (accounts.containsKey(accountNumber)) {
            return this.accounts.get(accountNumber);
        } else {
            return null;
        }
    }

    public String getAllAccountsInJsonFormat() {
        return new Gson().toJson(accounts);
    }
}
