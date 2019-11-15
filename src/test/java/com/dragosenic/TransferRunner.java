package com.dragosenic;

import com.dragosenic.servlet.MoneyTransferServlet;
import com.dragosenic.utilities.RND;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

public class TransferRunner {

    final int INITIAL_DEPOSIT;

    public TransferRunner(int INITIAL_DEPOSIT) {
        this.INITIAL_DEPOSIT = INITIAL_DEPOSIT;
    }

    private MoneyTransferServlet moneyTransferServlet(HttpMock http) {
        return new MoneyTransferServlet() {
            public ServletContext getServletContext() {
                return http.getServlet().getServletContext();
            }
        };
    }

    public void executeRandomMoneyTransfers(int moneyTransfersCount, ArrayList<Integer> accountNumbers, HttpMock httpMock) {

        try {

            for (int i = 0; i < moneyTransfersCount; i++) {
                String randomAmountToTransfer = String.format("%.2f", ((double)(RND.generateRandomInteger(1, INITIAL_DEPOSIT * 100)) / 100)); // <- random amount between 00.01 and INITIAL_DEPOSIT

                int randomAccountNumberFrom = 0;
                int randomAccountNumberTo = 0;
                do {
                    randomAccountNumberFrom = accountNumbers.get(RND.generateRandomInteger(0, accountNumbers.size() - 1));
                    randomAccountNumberTo = accountNumbers.get(RND.generateRandomInteger(0, accountNumbers.size() - 1));
                } while (randomAccountNumberFrom == randomAccountNumberTo);

                httpMock.mockPOST("{\"accountFrom\": " + randomAccountNumberFrom +
                        ", \"accountTo\": " + randomAccountNumberTo +
                        ", \"amount\": " + randomAmountToTransfer + "}");
                moneyTransferServlet(httpMock).doPost(httpMock.getRequest(), httpMock.getResponse());
            }

        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }

    }
}
