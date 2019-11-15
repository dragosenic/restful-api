package com.dragosenic;

import com.dragosenic.eBank.ElectronicBankingService;
import org.mockito.Mockito;

import javax.servlet.ServletContext;

public class ServletMock extends Mockito {

    final private ServletContext servletContext = Mockito.mock(ServletContext.class);

    public ServletMock(ElectronicBankingService eB) {
        when(servletContext.getAttribute("eB")).thenReturn(eB);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
