package com.dragosenic.servlet;

import com.dragosenic.data.InMemoryDB;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseServlet extends HttpServlet {

    protected InMemoryDB DB() {
        return (InMemoryDB)getServletContext().getAttribute("DB");
    }

    protected void serveTheResponse(HttpServletResponse response, String jsonString) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        response.getWriter().print(jsonString);
        response.getWriter().flush();
        response.getWriter().close();
    }

    protected void serveTheError(HttpServletResponse response, String errorMessage) throws IOException {
        System.out.println(errorMessage);

        JsonObject jsonError = new JsonObject();
        jsonError.addProperty("error", errorMessage);
        serveTheResponse(response, jsonError.toString());
    }

}
