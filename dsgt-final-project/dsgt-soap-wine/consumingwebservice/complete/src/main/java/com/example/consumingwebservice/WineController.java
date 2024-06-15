package com.example.consumingwebservice;

import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.consumingwebservice.wsdl.GetWineCardResponse;
import com.example.consumingwebservice.wsdl.Wine;

@RestController
public class WineController {

    @CrossOrigin(origins = "http://localhost:9090")
    @GetMapping("/wines")
    public List<Wine> getWines() {
        SoapClient soapClient = new SoapClient();
        GetWineCardResponse response = soapClient.getWine();
        if (response != null && response.getWine() != null) {
            return response.getWine();
        }
        return null;
    }
}
