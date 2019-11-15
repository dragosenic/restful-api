package com.dragosenic;

import com.dragosenic.data.InMemoryDB;
import com.dragosenic.eBank.ElectronicBanking;
import com.dragosenic.eBank.ElectronicBankingService;
import org.mockito.Mockito;

public class MockedBaseServlet extends Mockito {

    protected static ElectronicBankingService eB = new ElectronicBanking(new InMemoryDB());

}
