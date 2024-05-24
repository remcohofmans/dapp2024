package be.kuleuven.dsgt4;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Beverage {
    private String liquorShop;
    private UUID beverageId;
    private String customer;
    private String orderReference;
    private int price;

    public Beverage(){

    }

    public Beverage(String liquorShop, UUID beverageId, String customer, String orderReference, int price){
        this.liquorShop = liquorShop;
        this.beverageId = beverageId;
        this.customer = customer;
        this.orderReference = orderReference;
        this.price = price;
    }

    public String getLiquorShop() {
        return liquorShop;
    }

    public int getPrice() {
        return price;
    }

    public String getCustomer() {
        return customer;
    }

    public UUID getBeverageId() {
        return beverageId;
    }
    public String getOrderReference(){
        return orderReference;
    }

    public HashMap<String, String>(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("liquorShop", liquorShop);
        map.put("beverageId", beverageId.toString());
        map.put("price", price);
        map.put("customer", customer);
        map.put("orderReference", orderReference);
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Beverage)){
            return false;
        }
        var other = (Beverage) o;
        return this.customer.equals(other.customer)
                && this.beverageId.equals(other.beverageId)
                && this.price.equals(other.price)
                && this.liquorShop.equals(other.liquorShop)
                && this.orderReference.equals(other.orderReference);
    }

    public int hashCode(){ ///Still need to implement if needed
        return 0;
    }
}
