package be.kuleuven.dsgt4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    private RestTemplate restTemplate;

    // Define a method to proxy SOAP requests
    @PostMapping("/soap")
    public ResponseEntity<String> proxySoapRequest(@RequestBody String soapRequest) {
        // Define the SOAP endpoint URL
        String soapEndpointUrl = "http://dappvm.eastus.cloudapp.azure.com:12000/ws";

        // Set headers for the SOAP request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_XML);
        headers.set("SOAPAction", ""); // Set SOAPAction header if needed

        // Create HTTP entity with request body and headers
        HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

        // Send the SOAP request and get the response
        ResponseEntity<String> responseEntity = restTemplate.exchange(soapEndpointUrl, HttpMethod.POST, requestEntity, String.class);

        // Return the SOAP response to the client
        return responseEntity;
    }
}
