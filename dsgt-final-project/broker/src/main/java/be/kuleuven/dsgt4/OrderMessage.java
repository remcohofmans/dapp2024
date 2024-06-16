package be.kuleuven.dsgt4;

import com.google.cloud.firestore.annotation.PropertyName;
import java.time.LocalDateTime;
import java.util.UUID;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrderMessage implements Serializable {

    private UUID orderId;
    private LocalDateTime orderTime;
    private String customer;
    private List<String> items;
    private DeliveryInfo deliveryInfo;

    // No-argument constructor
    public OrderMessage() {
    }

    public OrderMessage(UUID orderId, LocalDateTime orderTime, String customer, List<String> items, DeliveryInfo deliveryInfo) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.customer = customer;
        this.items = items;
        this.deliveryInfo = deliveryInfo;
    }

    public UUID getOrderId() {
        return this.orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    @PropertyName("orderTime")
    public String getOrderTimeString() {
        return this.orderTime != null ? this.orderTime.format(DateTimeFormatter.ISO_DATE_TIME) : null;
    }

    @PropertyName("orderTime")
    public void setOrderTimeString(String orderTimeString) {
        this.orderTime = orderTimeString != null ? LocalDateTime.parse(orderTimeString, DateTimeFormatter.ISO_DATE_TIME) : null;
    }

    // Removed default getter for orderTime
    // public LocalDateTime getOrderTime() {
    //     return this.orderTime;
    // }

    // Removed default setter for orderTime
    // public void setOrderTime(LocalDateTime orderTime) {
    //     this.orderTime = orderTime;
    // }

    public String getCustomer() {
        return this.customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public List<String> getItems() {
        return this.items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public DeliveryInfo getDeliveryInfo() {
        return this.deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Map<String, Object> toDoc() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", this.orderId.toString());
        data.put("orderTime", this.getOrderTimeString());
        data.put("customer", this.customer);
        data.put("items", this.items);
        data.put("deliveryInfo", this.deliveryInfo.toDoc());

        return data;
    }

    public static OrderMessage fromDoc(Map<String, Object> doc) {
        String orderIdStr = (String) doc.get("orderId");
        String orderTimeStr = (String) doc.get("orderTime");
        String customer = (String) doc.get("customer");
        List<String> items = (List<String>) doc.get("items");
        Map<String, Object> deliveryInfoMap = (Map<String, Object>) doc.get("deliveryInfo");

        UUID orderId = orderIdStr != null ? UUID.fromString(orderIdStr) : null;
        LocalDateTime orderTime = orderTimeStr != null ? LocalDateTime.parse(orderTimeStr, DateTimeFormatter.ISO_DATE_TIME) : null;
        DeliveryInfo deliveryInfo = deliveryInfoMap != null ? DeliveryInfo.fromDoc(deliveryInfoMap) : null;

        return new OrderMessage(orderId, orderTime, customer, items, deliveryInfo);
    }

    public static class DeliveryInfo implements Serializable {
        private String address;
        private String deliveryTime;
        private String deliveryStatus;

        // No-argument constructor
        public DeliveryInfo() {
        }

        public DeliveryInfo(String address, String deliveryTime, String deliveryStatus) {
            this.address = address;
            this.deliveryTime = deliveryTime;
            this.deliveryStatus = deliveryStatus;
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @PropertyName("deliveryTime")
        public String getDeliveryTimeString() {
            return this.deliveryTime;
        }

        @PropertyName("deliveryTime")
        public void setDeliveryTimeString(String deliveryTimeString) {
            this.deliveryTime = deliveryTimeString;
        }



        public String getDeliveryStatus() {
            return this.deliveryStatus;
        }

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = deliveryStatus;
        }

        public Map<String, Object> toDoc() {
            Map<String, Object> data = new HashMap<>();
            data.put("address", this.address);
            data.put("deliveryTime", this.deliveryTime);
            data.put("deliveryStatus", this.deliveryStatus);

            return data;
        }

        public static DeliveryInfo fromDoc(Map<String, Object> doc) {
            String address = (String) doc.get("address");
            String deliveryTime = (String) doc.get("deliveryTime");
            String deliveryStatus = (String) doc.get("deliveryStatus");

            return new DeliveryInfo(address, deliveryTime, deliveryStatus);
        }
    }
}
