package be.kuleuven.dsgt4;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class Order {
    private List<Liquor> orderedLiquors = new LinkedList<>();
    private List<Delivery> deliveries = new LinkedList<>();
    private User user;
    private UUID orderId;
    private String deliveryAddress;
    private double totalPrice;
    private LocalDateTime orderTime;

    public Order(List<Liquor> beverageList, String specialInstruction, String deliveryAddress) {
        this.orderId = UUID.randomUUID();///////////////////
        this.orderedLiquors = beverageList;
        setTotalPrice();
        //this.user = UUID.randomUUID();
        this.orderTime = LocalDateTime.now();
        //this.liquorShops = liquorShops;
        this.deliveryAddress = deliveryAddress;

    }

   public double getTotalPrice() {return totalPrice;}
   public void setTotalPrice() {
       for (Liquor orderedLiquor : orderedLiquors) {
           totalPrice = totalPrice + orderedLiquor.getPrice();
       }
   }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }
    public List<Liquor> getOrderedLiquors() {return orderedLiquors;}

    public UUID getOrderId() {
        return orderId;
    }
    public String getDeliveryAddress() {return deliveryAddress;}

    public User getUser() {
        return user;
    }

    public HashMap<String, Object> getAsMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("orderNumber", orderId.toString());
        map.put("time", orderTime.toString());

        List<Map<String, String>> beverageMaps = orderedLiquors.stream()
                .map(Liquor::getAsMap)
                .collect(Collectors.toList());
        map.put("Liquors", beverageMaps);
        map.put("user", user.getEmail());

//        List<String> liquorShopNames = liquorShops.stream()
//                .map(LiquorShop::toString)
//                .collect(Collectors.toList());
//        map.put("liquorShops", liquorShopNames);

        return map;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", localDateTime=" + orderTime +
                ", liquorList=" + orderedLiquors +
                ", user=" + user.getEmail() +
                //", liquorShops=" + liquorShops +
                '}';
    }
}
