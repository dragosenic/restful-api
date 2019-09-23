package com.dragosenic.servlet;

import com.dragosenic.MockedBaseServlet;
import com.dragosenic.data.InMemoryDB;
import com.dragosenic.eBank.ElectronicBanking;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class AccountHolderServletTest extends MockedBaseServlet {

    private AccountHolderServlet sccountHolderServlet() {
        return new AccountHolderServlet() {
            public ServletContext getServletContext() { return servletContext; }
        };
    }

    @BeforeAll
    static void initDB() {
        MockedBaseServlet.eB = new ElectronicBanking(new InMemoryDB());
    }

    @Test
    void accountHolderServletTest1() throws Exception {

        // create account holder 1
        super.mockPOST("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
        sccountHolderServlet().doPost(request, response);

        printWriter.flush();
        Map result = new Gson().fromJson(responseWriter.toString(), Map.class);

        // assert
        Assertions.assertTrue(result.containsKey("accountHolderId"));
        Assertions.assertNotNull(result.get("accountHolderId"));
    }

    @Test
    void accountHolderServletTest2() throws Exception {

        // create account holder 2
        super.mockPOST("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
        sccountHolderServlet().doPost(request, response);

        // get all account holders (i.e. two of them)
        super.mockGET("/account-holder", new HashMap<>());
        sccountHolderServlet().doGet(request, response);

        printWriter.flush();
        ArrayList result = new Gson().fromJson(responseWriter.toString(), ArrayList.class);

        // assert
        Assertions.assertEquals(2, result.size());
    }

}