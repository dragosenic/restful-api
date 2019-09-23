package com.dragosenic.servlet;

import com.dragosenic.MockedBaseServlet;
import com.dragosenic.data.InMemoryDB;
import com.dragosenic.model.Account;
import com.dragosenic.eBank.ElectronicBanking;
import com.dragosenic.utilities.RND;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class MultiThreadMoneyTransferTest extends MockedBaseServlet {

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

    private static ArrayList<Integer> accountHolderIds = null; // <- will hold a value after test 1
    private static ArrayList<Integer> accountNumbers = null; // <- will hold a value after test 2

    private static final int ACCOUNTS_COUNT = 10;   // <- number of randomly created testing accounts
    private static final int INITIAL_DEPOSIT = 20;  // <- initial amount to deposit to all accounts
    private static final int TRANSFERS_COUNT_PER_THREAD = 200;  // <- number of random money transfers to execute per thread
    private static final int NUMBER_OF_THREADS = 5; // <- relevant only to test 4

    private String randomAmountToTransfer = "";

    @BeforeAll
    static void initDB() {
        MockedBaseServlet.eB = new ElectronicBanking(new InMemoryDB());
    }

    /**
     *  Create 4 account holders
     *
     *  @throws Exception
     */
    @Test
    void multiThreadMoneyTransferTest1() throws Exception {

        String[][] testdata = {
                {"John Lennon", "john@lennon.com"},
                {"Paul McCartney", "paul@mcca.co.uk"},
                {"George Harrison", "george@harrison.co.uk"},
                {"Ringo Starr", "ringo@starr.com"}};

        // create account holders
        for (int i = 0; i < testdata.length; i++) {
            super.mockPOST("{\"fullName\": \"" + testdata[i][0] + "\", \"emailPhoneAddress\": \"" + testdata[i][1] + "\"}");
            accountHolderServlet().doPost(request, response);
        }

        // get all account holders (i.e. four of them)
        super.mockGET("/account-holder", new HashMap<>());
        accountHolderServlet().doGet(request, response);

        printWriter.flush();
        ArrayList accountHolders = new Gson().fromJson(responseWriter.toString(), ArrayList.class);

        accountHolderIds = new ArrayList<>();
        for (int i = 0; i < accountHolders.size(); i++) {
            accountHolderIds.add(((Double)((LinkedTreeMap)accountHolders.get(i)).get("id")).intValue());
        }

        Assertions.assertEquals(testdata.length, accountHolderIds.size());
    }

    /**
     *  Create ACCOUNTS_COUNT different accounts
     *
     *  @throws Exception
     */
    @Test
    void multiThreadMoneyTransferTest2() throws Exception {

        String[] testdata = {"CHECKING", "CLASSIC", "SAVING", "BROKERAGE"};

        for (int i = 0; i < ACCOUNTS_COUNT; i++) {
            int randomAccountHolderId = accountHolderIds.get(RND.generateRandomInteger(0,accountHolderIds.size() - 1));
            String randomAccountType = testdata[RND.generateRandomInteger(0,testdata.length - 1)];

            super.mockPOST("{\"type\": \"" + randomAccountType + "\", \"accountHolder\": {\"id\": " + randomAccountHolderId + "}}");
            accountServlet().doPost(request, response);
        }

        // get all accounts
        super.mockGET("/account", new HashMap<>());
        accountServlet().doGet(request, response);

        printWriter.flush();
        HashMap accounts = new Gson().fromJson(responseWriter.toString(), HashMap.class);

        Assertions.assertEquals(ACCOUNTS_COUNT, accounts.size());

        accountNumbers = new ArrayList<Integer>();
        for (Object accountNumber : accounts.keySet()) {
            accountNumbers.add(Integer.parseInt((String)accountNumber));
        }
    }

    /**
     *  Deposit the same amount of INITIAL_DEPOSIT to all accounts
     *
     *  @throws Exception
     */
    @Test
    void multiThreadMoneyTransferTest3() throws Exception {

        for (int i = 0; i < accountNumbers.size(); i++) {
            super.mockPOST("{\"accountTo\": " + accountNumbers.get(i) + ", \"amount\": " + INITIAL_DEPOSIT + " }");
            moneyTransferServlet().doPost(request, response);
        }

        // get all accounts
        super.mockGET("/account", new HashMap<>());
        accountServlet().doGet(request, response);

        printWriter.flush();
        HashMap accounts = new Gson().fromJson(responseWriter.toString(), HashMap.class);

        Assertions.assertEquals(10, accounts.size());

        //
        for (int i = 0; i < accountNumbers.size(); i++) {
            HashMap<String, String> params = new HashMap<>();
            params.put("accountNumber", accountNumbers.get(i).toString());
            super.mockGET("/account", params);
            accountServlet().doGet(request, response);

            printWriter.flush();
            Account account = new Gson().fromJson(responseWriter.toString(), Account.class);

            Assertions.assertNotNull(account);
            Assertions.assertEquals(new BigDecimal(INITIAL_DEPOSIT + ".00"), account.getBalance());
        }

    }

    /**
     *  Execute money transfers from multiple threads
     *  perform TRANSFERS_COUNT random money transfers per thread
     *
     *  @throws Exception
     */
    @Test
    void multiThreadMoneyTransferTest4() throws Exception {

        ArrayList<Thread> threads = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.add(new Thread(() -> {
                System.out.println(String.format("started %s", Thread.currentThread().getName()));
                executeRandomMoneyTransfers(TRANSFERS_COUNT_PER_THREAD);
            }));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        // get all accounts
        super.mockGET("/account", new HashMap<>());
        accountServlet().doGet(request, response);

        printWriter.flush();
        HashMap accounts = new Gson().fromJson(responseWriter.toString(), HashMap.class);

        Assertions.assertEquals(10, accounts.size());

        // get each account separately and sums all balances
        BigDecimal totalSum = BigDecimal.ZERO;
        for (int i = 0; i < accountNumbers.size(); i++) {
            HashMap<String, String> params = new HashMap<>();
            params.put("accountNumber", accountNumbers.get(i).toString());
            super.mockGET("/account", params);
            accountServlet().doGet(request, response);

            printWriter.flush();
            Account account = new Gson().fromJson(responseWriter.toString(), Account.class);

            Assertions.assertNotNull(account);
            Assertions.assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) != -1); // <- no account balance will bi lees then zero

            totalSum = totalSum.add(account.getBalance());
        }

        // total sum of all account balances will remain constant
        Assertions.assertEquals(
                new BigDecimal(INITIAL_DEPOSIT).multiply(new BigDecimal(ACCOUNTS_COUNT)).setScale(2, RoundingMode.HALF_EVEN),
                totalSum);

    }

    private void executeRandomMoneyTransfers(int moneyTransfersCount) {

        try {

            for (int i = 0; i < moneyTransfersCount; i++) {
                synchronized (randomAmountToTransfer) {
                    randomAmountToTransfer = String.format("%.2f", ((double)(RND.generateRandomInteger(1, INITIAL_DEPOSIT * 100)) / 100)); // <- random amount between 00.01 and INITIAL_DEPOSIT

                    int randomAccountNumberFrom = 0;
                    int randomAccountNumberTo = 0;
                    do {
                        randomAccountNumberFrom = accountNumbers.get(RND.generateRandomInteger(0, accountNumbers.size() - 1));
                        randomAccountNumberTo = accountNumbers.get(RND.generateRandomInteger(0, accountNumbers.size() - 1));
                    } while (randomAccountNumberFrom == randomAccountNumberTo);

                    super.mockPOST("{\"accountFrom\": " + randomAccountNumberFrom +
                            ", \"accountTo\": " + randomAccountNumberTo +
                            ", \"amount\": " + randomAmountToTransfer + "}");
                    moneyTransferServlet().doPost(request, response);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }

}
