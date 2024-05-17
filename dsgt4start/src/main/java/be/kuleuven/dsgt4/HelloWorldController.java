package be.kuleuven.dsgt4;

import be.kuleuven.dsgt4.auth.WebSecurityConfig;
import be.kuleuven.dsgt4.externalServices.DeliveryService;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;



//import okhttp3.ResponseBody;


// Add the controller.
@RestController
class HelloWorldController {

    @Autowired
    Firestore db;

    @GetMapping("/api/hello")
    public String hello() {
        System.out.println("Inside hello");

        DeliveryService d = new DeliveryService();

        //d.getDeliveryInformationBasedOnID("44");

        info(d.getDeliveryInformationBasedOnID("44"));

        return info(d.getDeliveryInformationBasedOnID("44"));

        //return "this hello world!";
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


    public String info (String jsonString) {

        String information= "";

        System.out.println("tedtasgjhc");

        JSONObject jsonObject = new JSONObject(jsonString);
        String address = jsonObject.getString("address");
        double totalPrice = jsonObject.getDouble("totalPrice");
        String deliveryStatus = jsonObject.getString("deliveryStatus");
        String deliveryDateStr = jsonObject.getString("deliveryDate");
        String deliveryID = jsonObject.getString("deliveryID");

        JSONObject deliveryPerson = jsonObject.getJSONObject("deliveryPerson");
        String personName = deliveryPerson.getString("name");
        String personPhoneNumber = deliveryPerson.getString("phoneNumber");
        String personEmail = deliveryPerson.getString("email");
        String vehicleType = deliveryPerson.getString("vehicleType");
        JSONArray deliveryDates = deliveryPerson.getJSONArray("deliveryDates");

        JSONObject links = jsonObject.getJSONObject("_links");
        String selfLink = links.getJSONObject("self").getString("href");
        String overviewLink = links.getJSONObject("rest/delivery").getString("href");

        // Formatting the date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date deliveryDate = null;
        try {
            deliveryDate = sdf.parse(deliveryDateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        outputFormat.setTimeZone(TimeZone.getDefault());
        String formattedDate = outputFormat.format(deliveryDate);

        // Display the information


        System.out.println(" Delivery ID: " + deliveryID);
        information = information + "\n"+  " Address: " + address + "\n";
        information = information + " Total Price: €: " + address + "\n";
        information = information + " Delivery Date: " + formattedDate +"\n";


        String deliveryInfo = "Delivery Person:\n" +
                "  Name: " + personName + "\n" +
                "  Phone Number: " + personPhoneNumber + "\n" +
                "  Email: " + personEmail + "\n" +
                "  Vehicle Type: " + vehicleType;

        information += deliveryInfo;



        System.out.println("Address: " + address);
        System.out.println("Total Price: £" + String.format("%.2f", totalPrice));
        System.out.println("Delivery Status: " + deliveryStatus);
        System.out.println("Delivery Date: " + formattedDate);
        System.out.println("Delivery Person:");
        System.out.println("  Name: " + personName);
        System.out.println("  Phone Number: " + personPhoneNumber);
        System.out.println("  Email: " + personEmail);
        System.out.println("  Vehicle Type: " + vehicleType);
        System.out.println("Delivery Dates: ");
        for (int i = 0; i < deliveryDates.length(); i++) {
            try {
                Date date = sdf.parse(deliveryDates.getString(i));
                String formattedDeliveryDate = outputFormat.format(date);
                System.out.println("  - " + formattedDeliveryDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Links:");
        System.out.println("  Self: " + selfLink);
        System.out.println("  Overview of All Deliveries: " + overviewLink);

        return information;

}






}
