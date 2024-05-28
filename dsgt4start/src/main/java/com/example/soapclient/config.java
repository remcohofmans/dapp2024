package com.example.soapclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import com.example.soapclient.WineServiceClient;

@Configuration
public class config {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // This requires the package name generated during wsimport
        marshaller.setContextPath("com.example.soapclient");
        return marshaller;
    }

    @Bean
    public WineServiceClient wineServiceClient(Jaxb2Marshaller marshaller) {
        WineServiceClient client = new WineServiceClient();
        client.setDefaultUri("http://dapp.uksouth.cloudapp.azure.com:12000/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
