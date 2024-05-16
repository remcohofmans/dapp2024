package be.kuleuven.deliveryservice.domain;

import be.kuleuven.deliveryservice.exceptions.MealNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

@Component
public class DeliveryRepository {
    // map: id -> meal
    private static final Map<String, Deliver> orders = new HashMap<>();
    private int orderID;

    private static final double EUROPE_MIN_LATITUDE = 35.0;
    private static final double EUROPE_MAX_LATITUDE = 71.0;

    private static final double AFRICA_MIN_LATITUDE = -37.0;
    private static final double AFRICA_MAX_LATITUDE = 38.0;

    private static final double ASIA_MIN_LATITUDE = -11.0;
    private static final double ASIA_MAX_LATITUDE = 81.0;

    private static final double AMERICA_MIN_LATITUDE = -56.0;
    private static final double AMERICA_MAX_LATITUDE = 81.0;

    private static final double EUROPE_MIN_LONGITUDE = -25.0;
    private static final double EUROPE_MAX_LONGITUDE = 45.0;

    private static final double AFRICA_MIN_LONGITUDE = -25.0;
    private static final double AFRICA_MAX_LONGITUDE = 50.0;

    private static final double ASIA_MIN_LONGITUDE = 45.0;
    private static final double ASIA_MAX_LONGITUDE = 180.0;

    private static final double AMERICA_MIN_LONGITUDE = -180.0;
    private static final double AMERICA_MAX_LONGITUDE = -25.0;

    ArrayList<DeliveryPerson> deliveryPersonArrayList;



    @PostConstruct
    public void initData() {

        orderID = 0;

        deliveryPersonArrayList = new ArrayList<>();


        Deliver d = new Deliver();

        // American delivery guy
        DeliveryPerson deliveryPersonAmerica = new DeliveryPerson(
                "5268203c-de76-4921-a3e3-439db69c462a",
                "Jose Sanchez",
                "+32 495 55 60 65",
                "Jose.Sanchez@hotmail.com",
                VehicleType.CAR
        );

        DeliveryPerson deliveryPersonEurope = new DeliveryPerson(
                "4468203c-de76-4921-a3e3-439db69c462a",
                "Harry Wilson",
                "+44 495 55 60 65",
                "Harry.Wilson@hotmail.com",
                VehicleType.CAR
        );

        DeliveryPerson deliveryPersonAfrica = new DeliveryPerson(
                "2768203c-de76-4921-a3e3-439db69c462a",
                "Mobutu Nowana",
                "+27 223 55 68 95",
                "mobu17@gmail.com",
                VehicleType.CAR
        );

        DeliveryPerson deliveryPersonAsia = new DeliveryPerson(
                "6268203c-de76-4921-a3e3-439db69c462a",
                "Sari Lee",
                "+62 378 95 78 01",
                "sari.lee9@gmail.com",
                VehicleType.CAR
        );

        deliveryPersonArrayList.add(deliveryPersonEurope);
        deliveryPersonArrayList.add(deliveryPersonAfrica);
        deliveryPersonArrayList.add(deliveryPersonAsia);
        deliveryPersonArrayList.add(deliveryPersonAmerica);


        //Londen
        d.setAddress("51.5281798,-0.4312452");

        deliverService(chooseDeliveryPerson(d.getAddress()), d.getAddress(), "44");

        // south Afriqua
        d.setAddress("-29.1187116,25.6322336");
        deliverService(chooseDeliveryPerson(d.getAddress()), d.getAddress(), "33");

        d.setAddress("-29.1187116,25.6322336");
        deliverService(chooseDeliveryPerson(d.getAddress()), d.getAddress(), "43");


        //East US 41.6405232,-81.2218653 // change start address
//        d.setAddress("41.6405232,-81.2218653");
//        deliverService(chooseDeliveryPerson(d.getAddress()), d.getAddress());


    }

    private void deliverService(DeliveryPerson deliveryPerson, String address, String ID) {
        Deliver d2 = new Deliver();

        d2.setAddress(address);

        d2.setDeliveryPerson(deliveryPerson);
        //orderID++;
        d2.setOrderID(ID);

        d2.setTotalPrice(calculateTotalPrice(d2, deliveryPerson));

        //deliveryPerson.reserveDeliveryDatesByDeliveryID();

        d2.setDeliveryDate(deliveryPerson.getDeliveryDates().get(
                deliveryPerson.getDeliveryDates().size() - 1
        ));

        orders.put(d2.getDeliveryID(), d2);
    }

    public void addDelivery(String deliveryID, String coordination){
        Deliver d = new Deliver();
        d.setAddress(coordination);
        // need to add to check if id is already in use because it will create consitency problems
        d.setOrderID(deliveryID);
        deliverService(chooseDeliveryPerson(d.getAddress()), d.getAddress(), d.getDeliveryID());
    }

    public Collection<Deliver> getAllDelivries() {
        return orders.values();
    }

//    public void addDelivery(Deliver order) {
//        orderID++;
//        order.setOrderID(orderID);
//        order.setTotalPrice(calculateTotalPrice(order));
//        orders.put(order.getAddress(), order);
//    }

    private double calculateTotalPrice(Deliver order, DeliveryPerson dp){
        double price = 0.0;

        try{
            GoogleCloudMapHelper gmh = new  GoogleCloudMapHelper("51.5074,-0.1278", order.getAddress());

            String StringDistance  = gmh.getDistance();

            String numericValueString = StringDistance.replaceAll("[^\\d.]", "");

            // Convert the numeric value string to a double
            double distance = Double.parseDouble(numericValueString);

            price = 10 + (distance * 0.2 )*2;

            order.setDeliveryStatus(DeliveryStatus.PROCESSING);

            dp.reserveDeliveryDatesByDeliveryID(gmh.getDuration());

        }catch (Exception e){
            //System.out.println(e.toString());
            order.setDeliveryStatus(DeliveryStatus.UNABLE_TO_CALCULATE_PRICE_BECAUSE_OF_NETWORK_FAILURE_TO_ACCESS_GOOGLE_MAP_DISTANCE);
        }



        return price;

    }


    public Optional<Deliver> findOrder(String id) {
        for (Deliver m : orders.values()) {
            if(m.getDeliveryID().equals(id)){
                return Optional.of(m);
            }
        }
        return Optional.empty();

    }


    public void updateDeliveryStatus(String id, DeliveryStatus deliveryStatus) {
        for (Deliver m : orders.values()) {
            if(Objects.equals(m.getDeliveryID(), id)){
                m.setDeliveryStatus(deliveryStatus);
            }
        }

//        Deliver deliver = findOrder(id).orElseThrow(() -> new MealNotFoundException(String.valueOf(id)));
//
//        deliver.setDeliveryStatus(deliveryStatus);
    }

    public void deleteDelivery(String id) {

        Iterator<Map.Entry<String, Deliver>> iterator = orders.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, Deliver> entry = iterator.next();
            Deliver delivery = entry.getValue();

            if (Objects.equals(delivery.getDeliveryID(), id)) {
                DeliveryPerson deliveryPerson = delivery.getDeliveryPerson();

                if (deliveryPerson != null) {
                    List<Date> deliveryDates = deliveryPerson.getDeliveryDates();

                    if (deliveryDates != null && !deliveryDates.isEmpty()) {
                        deliveryDates.removeIf(date -> Objects.equals(date, delivery.getDeliveryDate()));
                    }
                }

                iterator.remove();
            }
        }
    }


    public DeliveryPerson chooseDeliveryPerson(String coordinates){

        String[] parts = coordinates.split(",");

        // Parse the latitude and longitude as doubles
        double latitude = Double.parseDouble(parts[0]);
        double longitude = Double.parseDouble(parts[1]);


        if (latitude >= EUROPE_MIN_LATITUDE && latitude <= EUROPE_MAX_LATITUDE && longitude >= EUROPE_MIN_LONGITUDE && longitude <= EUROPE_MAX_LONGITUDE) {
            System.out.println("Location is in Europe.");
            return deliveryPersonArrayList.get(0);
        } else if (latitude >= AFRICA_MIN_LATITUDE && latitude <= AFRICA_MAX_LATITUDE && longitude >= AFRICA_MIN_LONGITUDE && longitude <= AFRICA_MAX_LONGITUDE) {
            System.out.println("Location is in Africa.");
            return deliveryPersonArrayList.get(1);
        } else if (latitude >= ASIA_MIN_LATITUDE && latitude <= ASIA_MAX_LATITUDE && longitude >= ASIA_MIN_LONGITUDE && longitude <= ASIA_MAX_LONGITUDE) {
            System.out.println("Location is in Asia.");
            return deliveryPersonArrayList.get(2);
        } else if (latitude >= AMERICA_MIN_LATITUDE && latitude <= AMERICA_MAX_LATITUDE && longitude >= AMERICA_MIN_LONGITUDE && longitude <= AMERICA_MAX_LONGITUDE) {
            System.out.println("Location is in America.");
            return deliveryPersonArrayList.get(3);
        } else {
            System.out.println("Location is not within any defined continent range.");
        }





        return deliveryPersonArrayList.get(0);
    }


}
