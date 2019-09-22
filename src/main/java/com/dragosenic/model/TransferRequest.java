package com.dragosenic.model;

public class TransferRequest {

    private int accountFrom;

    private int accountTo;

    private double amount = 0;

    private String description;

    public TransferRequest(int accountFrom, int accountTo, double amount, String description) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
        this.description = description;
    }

    public int getAccountFrom() { return this.accountFrom; }

    public int getAccountTo() { return this.accountTo; }

    public double getAmount() { return this.amount; }

    public String getDescription() { return this.description; }
}
