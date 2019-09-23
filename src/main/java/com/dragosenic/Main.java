package com.dragosenic;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.data.InMemoryDB;
import com.dragosenic.eBank.ElectronicBanking;
import com.dragosenic.eBank.ElectronicBankingService;
import com.dragosenic.servlet.AccountHolderServlet;
import com.dragosenic.servlet.AccountServlet;
import com.dragosenic.servlet.MoneyTransferServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Drago Senic
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addEventListener(new ContextListener());

        context.addServlet(AccountServlet.class, "/account");
        context.addServlet(AccountHolderServlet.class, "/account-holder");
        context.addServlet(MoneyTransferServlet.class, "/money-transfer");

        context.addServlet(DefaultServlet.class, "/");

        server.setHandler(context);
        server.start();
        server.join();
    }

    public static class ContextListener implements ServletContextListener
    {
        @Override
        public void contextInitialized(ServletContextEvent sce)
        {
            sce.getServletContext().setAttribute("eB", new ElectronicBanking(new InMemoryDB()));

            ElectronicBankingService eB = (ElectronicBankingService)sce.getServletContext().getAttribute("eB");
            try {// insert some test data for account holders and accounts
                int accountHolderId1 = eB.createNewAccountHolder("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
                int accountHolderId2 = eB.createNewAccountHolder("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
                int accountHolderId3 = eB.createNewAccountHolder("{\"fullName\": \"George Harrison\", \"emailPhoneAddress\": \"george@harrison.co.uk\"}");
                int accountHolderId4 = eB.createNewAccountHolder("{\"fullName\": \"Ringo Starr\", \"emailPhoneAddress\": \"ringo@starr.co.uk\"}");

                int accountId1 = eB.createNewAccount("{\"type\": \"CHECKING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
                int accountId2 = eB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
                int accountId3 = eB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId2 + "}}");
            } catch (InvalidPostDataException e) {
                e.printStackTrace();
            }

            System.out.println("Context initialized!");
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce)
        {
            System.out.println("i am destroyed");
        }
    }
}
