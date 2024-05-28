package com.example.soapclient;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.example.soapclient.WineServiceClient;
import com.example.soapclient.GetMostExpensiveWineResponse;

@Service
public class SoapClientService {

    private final WineServiceClient wineServiceClient;

    public SoapClientService(WineServiceClient wineServiceClient) {
        this.wineServiceClient = wineServiceClient;
    }
    @Bean
    public String getMostExpensiveWine() {
        GetMostExpensiveWineResponse response = wineServiceClient.getMostExpensiveWine();
        // Extract and return the desired information from the response
        return response.getWine().getName();
    }
}
