package com.dragosenic.utilities;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public class MessageBody {

    private String data;

    public MessageBody(HttpServletRequest request) throws IOException {
        String line;
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        data = buffer.toString();
    }

    public String getData() { return data; }
}
