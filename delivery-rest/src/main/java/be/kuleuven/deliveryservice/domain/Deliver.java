package be.kuleuven.deliveryservice.domain;

import java.util.Date;

public class Deliver {

    protected String orderID;
    protected String address;
    protected double totalPrice;
    private DeliveryStatus deliveryStatus = DeliveryStatus.PROCESSING;
    private DeliveryPerson deliveryPerson;
    private Date deliveryDate;

    public String getDeliveryID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = String.valueOf(orderID);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public DeliveryPerson getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPerson deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}

