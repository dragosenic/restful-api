package com.dragosenic.eBank;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.data.AccountHolders;
import com.dragosenic.data.Accounts;

public interface ElectronicBankingService {

    AccountHolders getAccountHolders();

    Accounts getAccounts();

    int createNewAccount(String jsonData) throws InvalidPostDataException;

    int createNewAccountHolder(String jsonData) throws InvalidPostDataException;

    void createNewMoneyTransfer(String jsonData) throws InvalidPostDataException;

}
