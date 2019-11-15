package com.dragosenic.servlet;

import com.dragosenic.HttpMock;
import com.dragosenic.MockedBaseServlet;
import com.dragosenic.data.InMemoryDB;
import com.dragosenic.model.Account;
import com.dragosenic.eBank.ElectronicBanking;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

class MoneyTransferServletTest extends MockedBaseServlet {

    private AccountHolderServlet accountHolderServlet() {
        return new AccountHolderServlet() {
            public ServletContext getServletContext() { return http.getServlet().getServletContext(); }
        };
    }

    private AccountServlet accountServlet() {
        return new AccountServlet() {
            public ServletContext getServletContext() { return http.getServlet().getServletContext(); }
        };
    }

    private MoneyTransferServlet moneyTransferServlet() {
        return new MoneyTransferServlet() {
            public ServletContext getServletContext() { return http.getServlet().getServletContext(); }
        };
    }

    private static int accountHolderId1 = 0; // <- will hold a value after test 1
    private static int accountHolderId2 = 0; // <- will hold a value after test 1

    private static ArrayList<Integer> accountNumbers = null; // <- will hold a value after test 2

    private static HttpMock http;

    @BeforeAll
    static void initDB() {
        MockedBaseServlet.eB = new ElectronicBanking(new InMemoryDB());
        http = new HttpMock(MockedBaseServlet.eB);
    }

    /**
     *  Create two account holders
     *
     *  @throws Exception
     */
    @Test
    void accountHolderServletTest1() throws Exception {

        // create account holder 1
        http.mockPOST("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
        accountHolderServlet().doPost(http.getRequest(), http.getResponse());

        // create account holder 2
        http.mockPOST("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
        accountHolderServlet().doPost(http.getRequest(), http.getResponse());

        // get all account holders (i.e. two of them)
        http.mockGET("/account-holder", new HashMap<>());
        accountHolderServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        ArrayList accountHolders = new Gson().fromJson(http.getResponseWriter().toString(), ArrayList.class);

        accountHolderId1 = ((Double)((LinkedTreeMap)accountHolders.get(0)).get("id")).intValue();
        accountHolderId2 = ((Double)((LinkedTreeMap)accountHolders.get(1)).get("id")).intValue();

        Assertions.assertTrue(accountHolderId1 > 0 && accountHolderId2 > 0);
    }

    /**
     *  Create three accounts
     *
     *  @throws Exception
     */
    @Test
    void accountHolderServletTest2() throws Exception {

        // create account 1
        http.mockPOST("{\"type\": \"CHECKING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
        accountServlet().doPost(http.getRequest(), http.getResponse());

        // create account 1
        http.mockPOST("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
        accountServlet().doPost(http.getRequest(), http.getResponse());

        // create account 1
        http.mockPOST("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId2 + "}}");
        accountServlet().doPost(http.getRequest(), http.getResponse());

        // get all accounts (i.e. three of them)
        http.mockGET("/account", new HashMap<>());
        accountServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        HashMap accounts = new Gson().fromJson(http.getResponseWriter().toString(), HashMap.class);

        Assertions.assertEquals(3, accounts.size());

        accountNumbers = new ArrayList<Integer>();
        for (Object accountNumber : accounts.keySet()) {
            accountNumbers.add(Integer.parseInt((String)accountNumber));
        }
    }

    /**
     *  1. deposit 1000 to first account
     *  2. transfer 7.77 from first account to second account
     *
     *  @throws Exception
     */
    @Test
    void accountHolderServletTest3() throws Exception {

        http.mockPOST("{\"accountTo\": " + accountNumbers.get(0) + ", \"amount\": 1000 }");
        moneyTransferServlet().doPost(http.getRequest(), http.getResponse());

        http.mockPOST("{\"accountFrom\": " + accountNumbers.get(0) + ", \"accountTo\": " + accountNumbers.get(1) + ", \"amount\": 7.77 }");
        moneyTransferServlet().doPost(http.getRequest(), http.getResponse());

        // get all money transfers from firs account
        HashMap<String, String> params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(0).toString());
        http.mockGET("/account", params);
        accountServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        Account account = new Gson().fromJson(http.getResponseWriter().toString(), Account.class);

        // assert
        Assertions.assertTrue(account != null);
        Assertions.assertEquals(
                new BigDecimal("992.23"),
                account.getBalance());
    }

    /**
     *  tries to transfer 10 from second account to third account
     *  which should fail because there's no sufficient credentials
     *
     *  @throws Exception
     */
    @Test
    void accountHolderServletTest4() throws Exception {

        // 1. execute transfer from second to third account
        http.mockPOST("{\"accountFrom\": " + accountNumbers.get(1) + ", \"accountTo\": " + accountNumbers.get(2) + ", \"amount\": 10 }");
        moneyTransferServlet().doPost(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        Assertions.assertNotNull(http.getResponseWriter().toString());

        // 2. get all money transfers from second account (balance should stay 7.77)
        HashMap<String, String> params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(1).toString());
        http.mockGET("/account", params);
        accountServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        Account account = new Gson().fromJson(http.getResponseWriter().toString(), Account.class);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(
                new BigDecimal("7.77"),
                account.getBalance());

        // 3. get all money transfers from third account (balance should stay 0.00)
        params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(2).toString());
        http.mockGET("/account", params);
        accountServlet().doGet(http.getRequest(), http.getResponse());

        http.getPrintWriter().flush();
        account = new Gson().fromJson(http.getResponseWriter().toString(), Account.class);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(
                new BigDecimal("0.00"),
                account.getBalance());
    }

}