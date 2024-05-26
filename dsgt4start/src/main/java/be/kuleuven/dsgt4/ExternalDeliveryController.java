package be.kuleuven.dsgt4;

import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
public class ExternalDeliveryController {

    @Autowired
    Firestore db;

    private final WebClient webClient;

    @Autowired
    public ExternalDeliveryController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/api/askDelivery")
    public String hello() {
        try {
            String response = webClient.post()
                    .uri("http://localhost:8089/rest/delivery/8889/50.8748542,4.7051928")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

//            response.subscribe(
//                    res -> System.out.println("Response: " + res),
//                    err -> System.err.println("Error: " + err.getMessage())
//            );

            return webClient.get()
                    .uri("http://localhost:8089/rest/delivery/8889")  // Replace with the actual URL you want to call
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientResponseException e) {
            System.err.println("HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }


        // request to roll back all selected items

        return "Sorry, the delivery would not take place";  // Blocks and waits for the response
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