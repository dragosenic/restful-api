package com.dragosenic.data;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.utilities.RND;
import com.google.gson.Gson;

import java.util.ArrayList;

public class AccountHolders {

    private ArrayList<AccountHolder> accountHolders;

    AccountHolders() {
        accountHolders = new ArrayList<>();
    }

    int createNewAccountHolder(String fullName, String emailPhoneAddress) throws InvalidPostDataException {

        if (fullName == null || fullName.isEmpty()) {
            throw new InvalidPostDataException("Account Holder name not provided");
        }

        if (emailPhoneAddress == null || emailPhoneAddress.isEmpty()) {
            throw new InvalidPostDataException("Account Holder email, phone and the address is not provided");
        }

        int newId;
        do {
            newId = RND.generateNewAccountHolderId();
        } while (getAccountHolderById(newId) != null);

        AccountHolder accountHolder = new AccountHolder(newId);
        accountHolder.setFullName(fullName);
        accountHolder.setEmailPhoneAddress(emailPhoneAddress);

        accountHolders.add(accountHolder);

        return accountHolder.getId();
    }

    public AccountHolder getAccountHolderById(int id) {
        final int idToFind = id;
        return  accountHolders.stream()
               .filter(ah -> ah.getId() == idToFind)
               .findAny()
               .orElse(null);
    }

    public String getAllAccountHoldersInJsonFormat() {
        Gson gson = new Gson();
        return gson.toJson(accountHolders);
    }
}
