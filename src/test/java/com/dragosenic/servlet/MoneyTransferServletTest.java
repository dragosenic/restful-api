package com.dragosenic.servlet;

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
            public ServletContext getServletContext() { return servletContext; }
        };
    }

    private AccountServlet accountServlet() {
        return new AccountServlet() {
            public ServletContext getServletContext() { return servletContext; }
        };
    }

    private MoneyTransferServlet moneyTransferServlet() {
        return new MoneyTransferServlet() {
            public ServletContext getServletContext() { return servletContext; }
        };
    }

    private static int accountHolderId1 = 0; // <- will hold a value after test 1
    private static int accountHolderId2 = 0; // <- will hold a value after test 1

    private static ArrayList<Integer> accountNumbers = null; // <- will hold a value after test 2

    @BeforeAll
    static void initDB() {
        MockedBaseServlet.eB = new ElectronicBanking(new InMemoryDB());
    }

    /**
     *  Create two account holders
     *
     *  @throws Exception
     */
    @Test
    void accountHolderServletTest1() throws Exception {

        // create account holder 1
        super.mockPOST("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
        accountHolderServlet().doPost(request, response);

        // create account holder 2
        super.mockPOST("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
        accountHolderServlet().doPost(request, response);

        // get all account holders (i.e. two of them)
        super.mockGET("/account-holder", new HashMap<>());
        accountHolderServlet().doGet(request, response);

        printWriter.flush();
        ArrayList accountHolders = new Gson().fromJson(stringWriter.toString(), ArrayList.class);

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
        super.mockPOST("{\"type\": \"CHECKING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
        accountServlet().doPost(request, response);

        // create account 1
        super.mockPOST("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
        accountServlet().doPost(request, response);

        // create account 1
        super.mockPOST("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId2 + "}}");
        accountServlet().doPost(request, response);

        // get all accounts (i.e. three of them)
        super.mockGET("/account", new HashMap<>());
        accountServlet().doGet(request, response);

        printWriter.flush();
        HashMap accounts = new Gson().fromJson(stringWriter.toString(), HashMap.class);

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

        super.mockPOST("{\"accountTo\": " + accountNumbers.get(0) + ", \"amount\": 1000 }");
        moneyTransferServlet().doPost(request, response);

        super.mockPOST("{\"accountFrom\": " + accountNumbers.get(0) + ", \"accountTo\": " + accountNumbers.get(1) + ", \"amount\": 7.77 }");
        moneyTransferServlet().doPost(request, response);

        // get all money transfers from firs account
        HashMap<String, String> params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(0).toString());
        super.mockGET("/account", params);
        accountServlet().doGet(request, response);

        printWriter.flush();
        Account account = new Gson().fromJson(stringWriter.toString(), Account.class);

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
        super.mockPOST("{\"accountFrom\": " + accountNumbers.get(1) + ", \"accountTo\": " + accountNumbers.get(2) + ", \"amount\": 10 }");
        moneyTransferServlet().doPost(request, response);

        printWriter.flush();
        Assertions.assertNotNull(stringWriter.toString());

        // 2. get all money transfers from second account (balance should stay 7.77)
        HashMap<String, String> params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(1).toString());
        super.mockGET("/account", params);
        accountServlet().doGet(request, response);

        printWriter.flush();
        Account account = new Gson().fromJson(stringWriter.toString(), Account.class);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(
                new BigDecimal("7.77"),
                account.getBalance());

        // 3. get all money transfers from third account (balance should stay 0.00)
        params = new HashMap<>();
        params.put("accountNumber", accountNumbers.get(2).toString());
        super.mockGET("/account", params);
        accountServlet().doGet(request, response);

        printWriter.flush();
        account = new Gson().fromJson(stringWriter.toString(), Account.class);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(
                new BigDecimal("0.00"),
                account.getBalance());
    }

}