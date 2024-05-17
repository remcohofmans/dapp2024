package be.kuleuven.dsgt4.externalServices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.Date;
import java.text.ParseException;



public class DeliveryService {



    public String getDeliveryInformationBasedOnID(String deliverIdentity){
//        List<Deliver> deliveries = new ArrayList<>();
//
//        WebClient webclient = WebClient.builder().baseUrl("http://localhost:8089/rest/overviewOfAlldelivery").build();
//
//        Mono<JsonObject> monoDelivery = webclient.get().retrieve().bodyToMono(JsonObject.class);
//
//        JsonObject deliveryObject = monoDelivery.share().block();
//
//        JsonObject jsonObject = new Gson().fromJson(String.valueOf(deliveryObject), JsonObject.class);
//
//        JsonObject jsonObject1 = new Gson().fromJson(jsonObject.get("_embedded"), JsonObject.class);
//
//        JSONArray jsonArray = new Gson().fromJson(jsonObject1.get("deliveries"), JSONArray.class);
//
//        Gson gson = new Gson();
//
//        Map<String, Object>[] list = gson.fromJson(String.valueOf(jsonArray), Map[].class);
//
//
//        for(int i = 0; i < jsonArray.size(); i++){
//            String id = list[i].get("id").toString();
//            System.out.println(jsonArray.toJSONString());
//            System.out.println(id);
//        }

        //return deliveries;

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8089/rest/delivery/" + deliverIdentity)
                .build();

        String response = "";

        try {
            response = webClient.get()
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();


            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);


            // Access the "_embedded" object
            JsonNode embeddedNode = rootNode.get("_embedded");


            response = embeddedNode.toString();

            System.out.println(response);
        } catch (WebClientResponseException e) {
            System.err.println("HTTP error: " + e.getRawStatusCode());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        //System.out.println(response);

        return response;

    }


}
