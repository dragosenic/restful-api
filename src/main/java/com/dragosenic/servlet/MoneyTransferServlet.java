package com.dragosenic.servlet;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.utilities.MessageBody;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MoneyTransferServlet extends BaseServlet {

    /**
     *  Create money transfer between two accounts: accountFrom and accountTo
     *  or make deposit to accountTo if accountFrom is not provided.
     *
     *  Here is the format of json POST data to create money transfer:
     *  {
     *      "accountFrom": "1234567890",    // <- if accountFrom is not provided then it will only deposit money to accountTo
     *      "accountTo": "1234567891",
     *      "amount": "100.50",
     *      "description": "descr.";
     *  }
     */
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            synchronized (request){
                super.eB().createNewMoneyTransfer(new MessageBody(request).getData());
            }

            JsonObject toReturn = new JsonObject();
            toReturn.addProperty("moneyTransfer", true);
            super.serveTheResponse(response, toReturn.toString());

        } catch (InvalidPostDataException e) {

            super.serveTheError(response, e.toString());

        }
    }
}
