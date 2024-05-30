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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

//


//
@RestController
public class DeliveryController {

    @Autowired
    Firestore db;

    private final WebClient webClient;
    private User user;


    @Autowired
    public DeliveryController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();

    }

    @GetMapping("/api/askDelivery")
    public String hello() {
        try {
            String response = webClient.post()
                    .uri("http://dapp.southafricanorth.cloudapp.azure.com:13000/rest/delivery/f1e2d3c4-b5a6-7890-1234-56789abcdef0/8889/50.8748542,4.7051928")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            String block = webClient.get()
                    .uri("http://dapp.southafricanorth.cloudapp.azure.com:13000/rest/delivery/rest/f1e2d3c4-b5a6-7890-1234-56789abcdef0/overviewOfAlldelivery/8889")  // Replace with the actual URL you want to call
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            UUID orderId = UUID.randomUUID();
            LocalDateTime orderTime = LocalDateTime.now();
            String customer = user.getEmail();
            List<String> items = Arrays.asList("item1", "item2", "item3");
            OrderMessage.DeliveryInfo deliveryInfo = new OrderMessage.DeliveryInfo("123 Main St", "2023-06-01T12:00:00", "Pending");
            OrderMessage orderMessage = new OrderMessage(orderId, orderTime, customer, items, deliveryInfo);

            // Store OrderMessage in Firestore
            this.db.collection("orderMessage").document(orderMessage.getOrderId().toString()).set(orderMessage.toDoc()).get();


            return block;

        } catch (WebClientResponseException e) {
            System.err.println("HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }


        // request to roll back all selected items
        // increase quantities

        return "Sorry, the delivery would not take place";  // Blocks and waits for the response
    }

    @GetMapping("/api/whoami")
    public User whoami() throws InterruptedException, ExecutionException {
        user = WebSecurityConfig.getUser();
        if (!user.isManager()) throw new AuthorizationServiceException("You are not a manager");

        UUID buuid = UUID.randomUUID();
        UserMessage b = new UserMessage(buuid, LocalDateTime.now(), user.getRole(), user.getEmail());
        this.db.collection("usermessages").document(b.getId().toString()).set(b.toDoc()).get();

        return user;
    }

}