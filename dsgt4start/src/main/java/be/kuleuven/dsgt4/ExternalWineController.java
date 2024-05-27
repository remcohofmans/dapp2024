package be.kuleuven.dsgt4;

import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import be.kuleuven.dsgt4.SoapClient.GetWineCardRequest;
import be.kuleuven.dsgt4.SoapClient.GetWineCardResponse;
import be.kuleuven.dsgt4.SoapClient;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
public class ExternalDeliveryController {

    @Autowired
    Firestore db;

    private final SoapClient soapClient;

    @Autowired
    public ExternalDeliveryController(SoapClient soapClient) {
        this.soapClient = soapClient;
    }

    @GetMapping("/api/askWine")
    public String hello() {
        GetWineCardRequest request = new GetWineCardRequest();
        // Populate the request object if needed

        GetWineCardResponse response = soapClient.getWineCard(request);
        return response != null ? response.toString() : "No response from SOAP service";
    }

    @GetMapping("/api/whoami")
    public User whoami() throws InterruptedException, ExecutionException {
        var user = WebSecurityConfig.getUser();
        if (!user.isManager()) throw new AuthorizationServiceException("You are not a manager");

        UUID buuid = UUID.randomUUID();
        UserMessage b = new UserMessage(buuid, LocalDateTime.now(), user.getRole(), user.getEmail());
        this.db.collection("usermessages").document(b.getId().toString()).set(b.toDoc()).get();

        return user;
    }
}
