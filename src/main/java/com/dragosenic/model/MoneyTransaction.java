package com.dragosenic.model;

import com.dragosenic.common.MoneyDirection;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * MoneyTransaction POJO
 */
public class MoneyTransaction {

    private long timestamp;

    private MoneyDirection moneyDirection;

    private BigDecimal amount;

    private long accountNumber;

    private String description;

    public MoneyTransaction(long timestamp, MoneyDirection moneyDirection, BigDecimal amount, long accountNumber, String description) {
        this.timestamp = timestamp;
        this.moneyDirection = moneyDirection;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.accountNumber = accountNumber;
        this.description = description;
    }

    public long getTimestamp() { return this.timestamp; }

    public MoneyDirection getMoneyDirection() { return this.moneyDirection; }

    public long getAccountNumber() { return this.accountNumber; }

    public String getDescription() { return this.description; }

    public BigDecimal getAmount() { return this.amount; }
}
