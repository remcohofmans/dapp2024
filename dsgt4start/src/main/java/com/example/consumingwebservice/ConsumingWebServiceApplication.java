
package com.example.consumingwebservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.consumingwebservice.wsdl.GetWineCardResponse;
import com.example.consumingwebservice.wsdl.Wine;  // Ensure this import is correct
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@SpringBootApplication
public class ConsumingWebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumingWebServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner lookup(SoapClient soapClient) {
        return args -> {
            GetWineCardResponse response = soapClient.getWine();
            if (response != null && response.getWine() != null) {
                List<Wine> wines = response.getWine();
                ObjectMapper objectMapper = new ObjectMapper();
                wines.forEach(wine -> {
                    try {
                        String json = objectMapper.writeValueAsString(wine);
                        System.out.println(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                System.out.println("No response received or no wines available");
            }
        };
    }
}
