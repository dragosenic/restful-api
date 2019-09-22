package com.dragosenic;

import com.dragosenic.data.InMemoryDB;
import org.mockito.Mockito;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MockedBaseServlet extends Mockito {

    protected static InMemoryDB DB = new InMemoryDB();

    final protected HttpServletRequest request = mock(HttpServletRequest.class);
    final protected HttpServletResponse response = mock(HttpServletResponse.class);
    final protected ServletContext servletContext = Mockito.mock(ServletContext.class);

    protected PrintWriter printWriter = null;
    protected StringWriter stringWriter = null;

    protected void mockPOST(String jsonData) throws IOException {

        // mock request
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonData)));
        when(request.getContentType()).thenReturn("application/json");
        when(request.getCharacterEncoding()).thenReturn("UTF-8");

        // mock response
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // mock servletContext which holds instance of InMemoryDB
        when(servletContext.getAttribute("DB")).thenReturn(DB);
    }

    protected void mockGET(String pathInfo, HashMap<String, String> urlParameters) throws IOException {

        // mock request
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn(pathInfo);
        for (Map.Entry parameter : urlParameters.entrySet()) {
            when(request.getParameter((String)parameter.getKey())).thenReturn((String)parameter.getValue());
        }

        // mock response
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // mock servletContext which holds instance of InMemoryDB
        when(servletContext.getAttribute("DB")).thenReturn(DB);
    }

}