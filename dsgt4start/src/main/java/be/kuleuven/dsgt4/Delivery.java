package be.kuleuven.dsgt4;

import java.time.LocalDateTime;

public class Delivery {
    private String deliveryAddress;
    private String deliveryGuy;
    private LocalDateTime deliveryTime;


    Delivery(String deliveryAddress, String deliveryGuy, LocalDateTime deliveryTime){
        this.deliveryAddress = deliveryAddress;
        this.deliveryGuy = deliveryGuy;
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryAddress() {return deliveryAddress;}
    public String getDeliveryGuy() {return deliveryGuy;}
    public LocalDateTime getDeliveryTime() {return deliveryTime;}

    public void setDeliveryAddress(String deliveryAddress) {this.deliveryAddress = deliveryAddress;}
    public void setDeliveryGuy(String deliveryGuy) {this.deliveryGuy = deliveryGuy;}
    public void setDeliveryTime(LocalDateTime deliveryTime) {this.deliveryTime = deliveryTime;}
}
