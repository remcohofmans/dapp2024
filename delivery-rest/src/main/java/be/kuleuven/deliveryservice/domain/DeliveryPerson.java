package be.kuleuven.deliveryservice.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DeliveryPerson {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private VehicleType vehicleType;
    private DeliveryStatus available;
    private final ArrayList<Date> deliveryDates;
    private double longitude;
    private double latitude;
    //   private ArrayList<Date> availableDates;

    public DeliveryPerson(String id, String name, String phoneNumber, String email, VehicleType vehicleType) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.vehicleType = vehicleType;
        this.available = DeliveryStatus.PROCESSING;
        this.deliveryDates = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public DeliveryStatus isAvailable() {
        return available;
    }

    public void setAvailable(DeliveryStatus available) {
        this.available = available;
    }

    public ArrayList<Date> getDeliveryDates() {
        return deliveryDates;
    }

    public void reserveDeliveryDatesByDeliveryID(long deliveryduration){

        Date date = nextAvailableDate(deliveryDates, deliveryduration);

        // need to add some safe logic what if date is null
        deliveryDates.add(date);


    }

    // method first available date??? beter because then late possible to check which has the firstavailable date + location close to it


    public static Date nextAvailableDate(ArrayList<Date> reservedDates, long deliveryduration) {
        // Sort the reserved dates in ascending order
        Collections.sort(reservedDates);

        // Get today's date
        Date today = new Date();

        // Iterate through each reserved date
        for (Date reservedDate : reservedDates) {
            // If the reserved date is after today, return it as the next available date
            if (reservedDate.after(reservedDates.get(reservedDates.size() - 1))) {
                return reservedDate;
            }
        }


        // If all dates in the list are in the past, return the next day after the last reserved date
        if (!reservedDates.isEmpty()) {
            Date lastReservedDate = reservedDates.get(reservedDates.size() - 1);
            long nextDayMillis = lastReservedDate.getTime() + (24 * 60 * 60 * 1000
            + deliveryduration * 3
            ); // Adding one day's worth of milliseconds
            return new Date(nextDayMillis);
        }

        // If the reserved dates list is empty, return tomorrow's date
        return new Date(today.getTime() + (24 * 60 * 60 * 1000 + deliveryduration * 3));
    }





}