package com.dragosenic.eBank;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.data.AccountHolders;
import com.dragosenic.data.Accounts;
import com.dragosenic.model.Account;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.model.TransferRequest;

public interface DatabaseService {

    AccountHolders getAccountHolders();

    Accounts getAccounts();

    int createNewAccount(Account account) throws InvalidPostDataException;

    int createNewAccountHolder(AccountHolder accountHolder) throws InvalidPostDataException;

    void createNewMoneyTransfer(TransferRequest transferRequest) throws InvalidPostDataException;

}
