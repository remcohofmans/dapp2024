package be.kuleuven.dsgt4;

import be.kuleuven.dsgt4.*;
import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import com.example.soapclient.SoapClientService;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SpringBootApplication(scanBasePackages = {"com.example.soapclient", "be.kuleuven.dsgt4"})
public class SoapController {
    private final SoapClientService soapClientService;

    @Autowired
    Firestore db;

    SoapController(SoapClientService soapClientService) {
        this.soapClientService = soapClientService;
    }

    @GetMapping("/api/askWine")
    public String getWine() {
        return soapClientService.getMostExpensiveWine();
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
