package com.example.springsoap;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import io.liquormenu.gt.webservice.*;

@Component
public class LiquorRepository {
    private static final Map<String, Liquor> liquors = new HashMap<>();
    private static final Map<String, ArrayList<Liquor>> orders = new HashMap<>();
    private static final String FILE_NAME = "stock.txt";

    Random rand = new Random();

    @PostConstruct
    public void initData() {

        Liquor a = new Liquor();
        a.setBrand("HENDRICK'S gin");
        a.setPrice(35);
        a.setAlcoholPercentage(41.4f);
        a.setVolume(70);
        a.setQuantity(8);
        a.setType("Gin");

        liquors.put(a.getBrand(), a);

        Liquor b = new Liquor();
        b.setBrand("Bombay Sapphire");
        b.setPrice(19);
        b.setAlcoholPercentage(40);
        b.setVolume(70);
        b.setQuantity(7);
        b.setType("Gin");

        liquors.put(b.getBrand(), b);

        Liquor c = new Liquor();
        c.setBrand("CAMINO REAL");
        c.setPrice(21);
        c.setAlcoholPercentage(35);
        c.setVolume(70);
        c.setQuantity(4);
        c.setType("Tequila");

        liquors.put(c.getBrand(), c);

        Liquor d = new Liquor();
        d.setBrand("SIERRA SILVER");
        d.setPrice(19);
        d.setAlcoholPercentage(38);
        d.setVolume(70);
        d.setQuantity(4);
        d.setType("Tequila");

        liquors.put(d.getBrand(), d);

        Liquor e = new Liquor();
        e.setBrand("Johnnie Walker Blue Label");
        e.setPrice(165);
        e.setAlcoholPercentage(40);
        e.setVolume(70);
        e.setQuantity(3);
        e.setType("Whiskey");

        liquors.put(e.getBrand(), e);

        Liquor f = new Liquor();
        f.setBrand("Glenfiddich Our Original Twelve");
        f.setPrice(40);
        f.setAlcoholPercentage(40);
        f.setVolume(70);
        f.setQuantity(10);
        f.setType("Whiskey");

        liquors.put(f.getBrand(), f);

        Liquor g = new Liquor();
        g.setBrand("Grey Goose");
        g.setPrice(35);
        g.setAlcoholPercentage(40);
        g.setVolume(70);
        g.setQuantity(6);
        g.setType("Vodka");

        liquors.put(g.getBrand(), g);

        Liquor h = new Liquor();
        h.setBrand("Eristoff");
        h.setPrice(16);
        h.setAlcoholPercentage(37.5f);
        h.setVolume(70);
        h.setQuantity(10);
        h.setType("Vodka");

        liquors.put(h.getBrand(), h);

        Liquor i = new Liquor();
        i.setBrand("Hennesey X.o");
        i.setPrice(200);
        i.setAlcoholPercentage(40);
        i.setVolume(75);
        i.setQuantity(2);
        i.setType("Cognac");

        liquors.put(i.getBrand(), i);

        Liquor j = new Liquor();
        j.setBrand("Remy Martin X.O");
        j.setPrice(164);
        j.setAlcoholPercentage(40);
        j.setVolume(70);
        j.setQuantity(5);
        j.setType("Cognac");

        liquors.put(j.getBrand(), j);
    }


    public void writeStock() {
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            for (Liquor liquor : liquors.values()) {
                writer.write(liquor.getBrand() + ": " + liquor.getQuantity() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Liquor findLiquor(String brand) {
        Assert.notNull(brand, "The liquor's brand must not be null");
        return liquors.get(brand);
    }

    public Liquor findCheapestLiquor() {
        if (liquors == null) return null;
        if (liquors.size() == 0) return null;

        var values = liquors.values();
        return values.stream().min(Comparator.comparing(Liquor::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public Liquor findMostExpensiveLiquor() {
        if (liquors == null) return null;
        if (liquors.size() == 0) return null;

        return liquors.values().stream().max(Comparator.comparing(Liquor::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public Order createOrder(List<String> liquorList, String specialInstructions, String deliveryAddress) {

        Order order = new Order();

        order.setOrderNumber(rand.nextLong(99999) + 1L);
        order.setSpecialInstructions(specialInstructions);
        order.setTimestamp(LocalDateTime.now().toString());
        order.setDeliveryAddress(deliveryAddress);

        ArrayList<Liquor> liquorOrder = new ArrayList<>();
        for (String liquorBrand : liquorList) {
            Liquor liquor = findLiquor(liquorBrand);
            if (liquor != null) {
                liquorOrder.add(liquor);
            } else {
                // Handle if liquor brand is not found
                System.out.println("The liquor: " + liquorBrand + " was not found. This liquor has not been added to your order.");
            }
        }

        // Add the order to the orders map
        orders.put(String.valueOf(order.getOrderNumber()), liquorOrder);

        return order;
    }

    public void setLiquors(List<Liquor> liquors) {
        for (Liquor liquor : liquors) {
            this.liquors.put(liquor.getBrand(), liquor);
        }
    }

    public List<Liquor> getLiquorCard() {
        return new ArrayList<Liquor>(liquors.values());
    }

    public void addLiquor(Liquor liquor) {
        liquors.put(liquor.getBrand(), liquor);
    }

}
