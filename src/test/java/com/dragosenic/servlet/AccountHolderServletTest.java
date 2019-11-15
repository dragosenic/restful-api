package com.dragosenic.servlet;

import com.dragosenic.HttpMock;
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
            public ServletContext getServletContext() { return http.getServlet().getServletContext(); }
        };
    }

    private static HttpMock http;

    @BeforeAll
    static void initDB() {
        MockedBaseServlet.eB = new ElectronicBanking(new InMemoryDB());
        http = new HttpMock(MockedBaseServlet.eB);
    }

    @Test
    void accountHolderServletTest1() throws Exception {

        // create account holder 1
        http.mockPOST("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
        sccountHolderServlet().doPost(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        Map result = new Gson().fromJson(http.getResponseWriter().toString(), Map.class);

        // assert
        Assertions.assertTrue(result.containsKey("accountHolderId"));
        Assertions.assertNotNull(result.get("accountHolderId"));
    }

    @Test
    void accountHolderServletTest2() throws Exception {

        // create account holder 2
        http.mockPOST("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
        sccountHolderServlet().doPost(http.getRequest(), http.getResponse());

        // get all account holders (i.e. two of them)
        http.mockGET("/account-holder", new HashMap<>());
        sccountHolderServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        ArrayList result = new Gson().fromJson(http.getResponseWriter().toString(), ArrayList.class);

        // assert
        Assertions.assertEquals(2, result.size());
    }

}