package be.kuleuven.dsgt4;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class Order {
    private UUID orderNumber;
    private LocalDateTime localDateTime;
    private List<Liquor> beveragelist;
    private UUID customerId;
    //private List<LiquorShop> liquorShops;
    private String deliveryAddress;

    public Order(List<Liquor> beverageList, String specialInstruction, String deliveryAddress) {
        this.orderNumber = UUID.randomUUID();///////////////////
        this.beveragelist = beverageList;
        this.customerId = UUID.randomUUID();
        this.localDateTime = LocalDateTime.now();
        //this.liquorShops = liquorShops;
        this.deliveryAddress = deliveryAddress;

    }

    public UUID getOrderNumber() {
        return this.orderNumber;
    }

    public LocalDateTime getTime() {
        return this.localDateTime;
    }

    public List<Liquor> getBeverages() {
        return this.beveragelist;
    }

    public UUID getCustomer() {
        return this.customerId;
    }

    public String getDeliveryAddress() {return this.deliveryAddress;}
    public void addBeverage(Liquor beverage) {
        this.beveragelist.add(beverage);
    }

    public void setBeveragelist(List<Liquor> beveragelist) {
        this.beveragelist = beveragelist;
    }

    public HashMap<String, Object> getAsMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderNumber", orderNumber.toString());
        map.put("time", localDateTime.toString());

        List<Map<String, String>> beverageMaps = beveragelist.stream()
                .map(Liquor::getAsMap)
                .collect(Collectors.toList());
        map.put("Liquors", beverageMaps);
        map.put("customerId", customerId.toString());

//        List<String> liquorShopNames = liquorShops.stream()
//                .map(LiquorShop::toString)
//                .collect(Collectors.toList());
//        map.put("liquorShops", liquorShopNames);

        return map;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderNumber +
                ", localDateTime=" + localDateTime +
                ", liquorList=" + beveragelist +
                ", customerId=" + customerId +
                //", liquorShops=" + liquorShops +
                '}';
    }
}
