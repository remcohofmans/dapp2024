package be.kuleuven.dsgt4.externalServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class DeliveryPerson {

    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private DeliveryStatus available;

    public DeliveryPerson(String id, String name, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.available = DeliveryStatus.PROCESSING;
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

    public DeliveryStatus isAvailable() {
        return available;
    }

    public void setAvailable(DeliveryStatus available) {
        this.available = available;
    }

}