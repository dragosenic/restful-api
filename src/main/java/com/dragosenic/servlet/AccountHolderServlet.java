package com.dragosenic.servlet;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.model.AccountHolder;
import com.dragosenic.utilities.MessageBody;
import com.dragosenic.utilities.UrlParameter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AccountHolderServlet extends BaseServlet {

    /**
     *  Return account holder by id
     *  or
     *  Return all account holders if id is not provided.
     *
     *  here is the url format:
     *  /account-holder?id=1234567890  <-  to get account holder by id
     *  /account  <-  to get all account holders
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        UrlParameter id = new UrlParameter(request.getParameter("id"));

        if (id.isNull()) {

            super.serveTheResponse(response, super.eB().getAccountHolders().getAllAccountHoldersInJsonFormat());

        } else if (id.isInteger()) {

            AccountHolder accountHolder = super.eB().getAccountHolders().getAccountHolderById(id.asInteger());
            if (accountHolder != null) {
                super.serveTheResponse(response, new Gson().toJson(accountHolder));
            } else {
                super.serveTheError(response, "account holder not found");
            }

        } else {
            super.serveTheError(response, "provided id parameter is not an integer");
        }
    }

    /**
     *  Create new account holder and return id of newly created account holder
     *
     *  here is the json data to POST to create new account holder:
     *  {
     * 	    "fullName": "John Lennon",
     * 	    "emailPhoneAddress": "john@lennon.com, 0123-456, 7 Great western rd."
     *  }
     *
     *  NOTE: In order to successfully create new account holder,
     *  fullName id and emailPhoneAddress must have values
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            int newAccountHolderId = super.eB().createNewAccountHolder(new MessageBody(request).getData());

            JsonObject toReturn = new JsonObject();
            toReturn.addProperty("accountHolderId", newAccountHolderId);
            super.serveTheResponse(response, toReturn.toString());

        } catch (InvalidPostDataException e) {

            super.serveTheError(response, e.toString());

        }
    }
}
