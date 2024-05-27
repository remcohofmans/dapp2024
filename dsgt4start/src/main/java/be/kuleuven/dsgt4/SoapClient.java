package be.kuleuven.dsgt4;


import org.springframework.ws.client.core.WebServiceTemplate;

public class SoapClient {

    private final WebServiceTemplate webServiceTemplate;

    public SoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public GetWineCardResponse getWineCard(GetWineCardRequest request) {
        return (GetWineCardResponse) webServiceTemplate.marshalSendAndReceive(request);
    }
}
