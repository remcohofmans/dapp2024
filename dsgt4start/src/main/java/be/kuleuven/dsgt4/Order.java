package be.kuleuven.dsgt4;

import org.threeten.bp.LocalDateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Order {
    private UUID id;
    private LocalDateTime localDateTime;
    private List<Beverage> beveragelist;
    private String customer;

    public Order(UUID id, LocalDateTime localDateTime, List<Beverage> beverageList, String customer){
        this.id = id;
        this.localDateTime = localDateTime;
        this.beveragelist = beverageList;
        this.customer = customer;
    }

    public UUID getId(){
        return this.id;
    }

    public HashMap<String, Object > getAsMap(){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", id.toString());
        map.put("time", localDateTime.toString());
        LinkedList<HashMap<String, String>> list = new LinkedList<HashMap<String, String>>();
        for (Beverage beverage : beveragelist){
            list.add(beverage.getAsMap());
        }
        map.put("beverages", list);
        map.put("customer", customer);
        return map;
    }

    public LocalDateTime getTime(){
        return this.localDateTime;
    }

    public List<Beverage> getBeverages(){
        return this.beveragelist;
    }

    public void setBeveragelist(List<Beverage> beveragelist){
        this.beveragelist = beveragelist;
    }

    public String getCustomer(){
        return this.customer;
    }
}
