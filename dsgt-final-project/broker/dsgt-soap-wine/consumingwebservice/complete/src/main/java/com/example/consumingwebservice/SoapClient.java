
package com.example.consumingwebservice;


import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import com.example.consumingwebservice.wsdl.GetWineCardRequest;
import com.example.consumingwebservice.wsdl.GetWineCardResponse;

public class SoapClient extends WebServiceGatewaySupport {

    public GetWineCardResponse getWine() {
        GetWineCardRequest request = new GetWineCardRequest();

        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.consumingwebservice.wsdl"); // Set the package where your generated classes are located
        try {
			marshaller.afterPropertiesSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Set marshaller and unmarshaller
        this.setMarshaller(marshaller);
        this.setUnmarshaller(marshaller);

        return (GetWineCardResponse) getWebServiceTemplate()
                .marshalSendAndReceive("http://dapp.uksouth.cloudapp.azure.com:12000/ws/wines", request,
                        new SoapActionCallback("http://spring.io/guides/gs-producing-web-service/GetWineCardRequest"));
    }
}
