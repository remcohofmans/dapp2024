package be.kuleuven.deliveryservice.domain;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GoogleCloudMapHelper {


    private String distance;
    private String duration;

    public GoogleCloudMapHelper( String origin,  String destination){

        String apiKey = "AIzaSyBFR-QFJG4IC8k55TtTE7ClBzMyXWYUJTo"; // Replace with your Google Maps API key

        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="
                    + origin + "&destinations=" + destination + "&key=" + apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            parseDistanceFromJson(jsonResponse);

            System.out.println("Distance: " + distance);
            System.out.println("Duration: " + duration);


        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void parseDistanceFromJson(String jsonResponse) {

        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONArray rows = data.getJSONArray("rows");
            JSONObject firstRow = rows.getJSONObject(0);
            JSONArray elements = firstRow.getJSONArray("elements");
            JSONObject firstElement = elements.getJSONObject(0);

            distance = firstElement.getJSONObject("distance").getString("text");
            duration = firstElement.getJSONObject("duration").getString("text");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDistance() {
        return distance;
    }

    public long getDuration() {
        return convertTimeStringToSeconds(duration);
    }

    public static long convertTimeStringToSeconds(String timeString) {
        long totalSeconds = 0;
        String[] components = timeString.split("\\s+");

        for (int i = 0; i < components.length; i += 2) {
            int quantity = Integer.parseInt(components[i]);
            String unit = components[i + 1];

            switch (unit) {
                case "days":
                    totalSeconds += quantity * 24 * 60 * 60;
                    break;
                case "hours":
                    totalSeconds += quantity * 60 * 60;
                    break;
                case "min":
                    totalSeconds += quantity * 60;
                    break;
            }
        }

        return totalSeconds;
    }

}