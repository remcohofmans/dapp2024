package com.example.soapclient;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import com.example.soapclient.GetMostExpensiveWineRequest;
import com.example.soapclient.GetMostExpensiveWineResponse;

public class WineServiceClient extends WebServiceGatewaySupport {

    public GetMostExpensiveWineResponse getMostExpensiveWine() {
        GetMostExpensiveWineRequest request = new GetMostExpensiveWineRequest();
        // Add any necessary parameters to the request here

        return (GetMostExpensiveWineResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }
}
