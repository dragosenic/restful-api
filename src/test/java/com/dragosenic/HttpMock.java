package com.dragosenic;

import com.dragosenic.eBank.ElectronicBankingService;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpMock extends Mockito {

    private HttpServletRequest request;
    private HttpServletResponse response;

    private ServletMock servlet;

    private StringWriter responseWriter = null;
    private PrintWriter printWriter = null;

    private ElectronicBankingService eB;

    public HttpMock(ElectronicBankingService eB) {
        this.eB = eB;
        this.servlet = new ServletMock(eB);
    }

    public void mockPOST(String jsonData) throws IOException {

        // mock request
        request = mock(HttpServletRequest.class);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonData)));
        when(request.getContentType()).thenReturn("application/json");
        when(request.getCharacterEncoding()).thenReturn("UTF-8");

        // mock response
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // mock servletContext which holds instance of InMemoryDB
        when(servlet.getServletContext().getAttribute("eB")).thenReturn(eB);
    }

    public void mockGET(String pathInfo, HashMap<String, String> urlParameters) throws IOException {

        // mock request
        request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn(pathInfo);
        for (Map.Entry parameter : urlParameters.entrySet()) {
            when(request.getParameter((String)parameter.getKey())).thenReturn((String)parameter.getValue());
        }

        // mock response
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // mock servletContext which holds instance of InMemoryDB
        when(servlet.getServletContext().getAttribute("eB")).thenReturn(eB);
    }

    public HttpServletRequest getRequest() { return request; }

    public HttpServletResponse getResponse() { return response; }

    public ServletMock getServlet() { return servlet; }

    public StringWriter getResponseWriter() { return responseWriter; }

    public PrintWriter getPrintWriter() { return printWriter; }

}
