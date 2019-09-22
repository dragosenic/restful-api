package com.dragosenic.model;

/**
 * AccountHolder POJO
 */
public class AccountHolder {

    private int id = 0;

    private String fullName;

    private String emailPhoneAddress;

    public AccountHolder(int id) {
        this.id = id;
    }

    public int getId() { return this.id; }


    public String getFullName() { return this.fullName; }

    public void setFullName(String value) { this.fullName = value; }

    public String getEmailPhoneAddress() { return this.emailPhoneAddress; }

    public void setEmailPhoneAddress(String value) { this.emailPhoneAddress = value; }
}