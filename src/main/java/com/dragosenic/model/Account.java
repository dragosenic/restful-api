package com.dragosenic.model;

import com.dragosenic.common.AccountType;
import com.dragosenic.data.MoneyTransactions;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Account POJO
 */
public class Account {

    private int accountNumber = 0;

    private AccountType type;

    private AccountHolder accountHolder;

    private MoneyTransactions moneyTransactions;

    private BigDecimal balance;

    public Account(int accountNumber, AccountHolder accountHolder) {
        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.moneyTransactions = new MoneyTransactions();
    }

    public int getAccountNumber() { return this.accountNumber; }

    public AccountType getType() {
        return this.type;
    }

    public void setType(AccountType value) {
        this.type = value;
    }

    public AccountHolder getAccountHolder() { return this.accountHolder; }

    public MoneyTransactions getMoneyTransactions() { return moneyTransactions; }

    public BigDecimal getBalance() { return moneyTransactions.getBalance().setScale(2, RoundingMode.HALF_EVEN); }
    public void setBalance(BigDecimal v) { this.balance = v; }
}