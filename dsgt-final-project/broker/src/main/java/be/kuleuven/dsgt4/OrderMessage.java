package be.kuleuven.dsgt4;

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

    public LocalDateTime getOrderTime() {
        return this.orderTime;
    }

    public String getCustomer() {
        return this.customer;
    }

    public List<String> getItems() {
        return this.items;
    }

    public DeliveryInfo getDeliveryInfo() {
        return this.deliveryInfo;
    }

    public Map<String, Object> toDoc() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", this.orderId.toString());
        data.put("orderTime", this.orderTime.format(DateTimeFormatter.ISO_DATE_TIME));
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

        public DeliveryInfo(String address, String deliveryTime, String deliveryStatus) {
            this.address = address;
            this.deliveryTime = deliveryTime;
            this.deliveryStatus = deliveryStatus;
        }

        public String getAddress() {
            return this.address;
        }

        public String getDeliveryTime() {
            return this.deliveryTime;
        }

        public String getDeliveryStatus() {
            return this.deliveryStatus;
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