package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.common.MoneyDirection;
import com.dragosenic.model.MoneyTransaction;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class MoneyTransactions {

    private ArrayList<MoneyTransaction> moneyTransactions;

    public MoneyTransactions() {
        this.moneyTransactions = new ArrayList<>();
    }

    void createMoneyTransaction(long timestamp, MoneyDirection moneyDirection, BigDecimal amount, long accountNumber, String description) throws InvalidPostDataException {
        if (amount.compareTo(new BigDecimal("0.00")) == 1) {
            moneyTransactions.add(
                new MoneyTransaction(timestamp, moneyDirection, amount, accountNumber, description)
            );
        } else {
            throw new InvalidPostDataException("Amount to transfer is equal or less than zero");
        }
    }

    public BigDecimal getBalance() {
        return moneyTransactions.stream()
                .map(t -> t.getMoneyDirection().equals(MoneyDirection.IN) ? t.getAmount().setScale(2, RoundingMode.HALF_EVEN) : t.getAmount().negate().setScale(2, RoundingMode.HALF_EVEN))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public String getAllMoneyTransfersInJsonFormat() {
        Gson gson = new Gson();
        return gson.toJson(moneyTransactions);
    }

}
