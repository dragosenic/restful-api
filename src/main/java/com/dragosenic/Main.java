package com.dragosenic;

import com.dragosenic.common.InvalidPostDataException;
import com.dragosenic.data.InMemoryDB;
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
            sce.getServletContext().setAttribute("DB", new InMemoryDB());

            InMemoryDB DB = (InMemoryDB)sce.getServletContext().getAttribute("DB");
            try {// insert some test data for account holders and accounts
                int accountHolderId1 = DB.createNewAccountHolder("{\"fullName\": \"John Lennon\", \"emailPhoneAddress\": \"john@lennon.com\"}");
                int accountHolderId2 = DB.createNewAccountHolder("{\"fullName\": \"Paul McCartney\", \"emailPhoneAddress\": \"paul@mcca.co.uk\"}");
                int accountHolderId3 = DB.createNewAccountHolder("{\"fullName\": \"George Harrison\", \"emailPhoneAddress\": \"george@harrison.co.uk\"}");
                int accountHolderId4 = DB.createNewAccountHolder("{\"fullName\": \"Ringo Starr\", \"emailPhoneAddress\": \"ringo@starr.co.uk\"}");

                int accountId1 = DB.createNewAccount("{\"type\": \"CHECKING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
                int accountId2 = DB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId1 + "}}");
                int accountId3 = DB.createNewAccount("{\"type\": \"SAVING\", \"accountHolder\": {\"id\": " + accountHolderId2 + "}}");
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
