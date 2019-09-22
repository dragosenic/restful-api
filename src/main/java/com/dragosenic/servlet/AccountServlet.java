package com.dragosenic.servlet;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.model.Account;
import com.dragosenic.utilities.MessageBody;
import com.dragosenic.utilities.UrlParameter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountServlet extends BaseServlet {

    /**
     *  Return account by accountNumber or
     *  Return all accounts if accountNumber is not provided.
     *
     *  here is the url format:
     *  /account?accountNumber=1234567890  <-  to get account by accountNumber
     *  /account  <-  to get all accounts
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UrlParameter accountNumber = new UrlParameter(request.getParameter("accountNumber"));

        if (accountNumber.isNull()) {

            super.serveTheResponse(response, super.DB().getAccounts().getAllAccountsInJsonFormat());

        } else if (accountNumber.isInteger()) {

            Account account = super.DB().getAccounts().getAccountById(accountNumber.asInteger());
            if (account != null) {
                super.serveTheResponse(response, new Gson().toJson(account));
            } else {
                super.serveTheError(response, "Account not found");
            }

        } else {

            super.serveTheError(response, "Provided accountNumber parameter is not an integer");

        }
    }

    /**
     *  Create new account and return accountNumber of newly created account.
     *
     *  here is the format of json POST data to create a new account:
     *  {
     * 	    "type": "SAVING",
     * 	    "accountHolder": {"id": "1234567890"}
     *  }
     *
     *  NOTE: In order to successfully create new account,
     *  account holder id and account type must have valid values
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            int newAccountNumber = super.DB().createNewAccount(new MessageBody(request).getData());

            JsonObject toReturn = new JsonObject();
            toReturn.addProperty("accountNumber", newAccountNumber);
            super.serveTheResponse(response, toReturn.toString());

        } catch (InvalidPostDataException e) {

            super.serveTheError(response, e.toString());

        }
    }

}