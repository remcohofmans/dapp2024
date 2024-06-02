package com.example.springsoap;

import javax.annotation.PostConstruct;
import java.sql.Array;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.time.LocalDateTime;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;


import io.winemenu.gt.webservice.*;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class WineRepository {
    private static final Map<String, Wine> wines = new HashMap<String, Wine>();
    private static final Map<String, ArrayList<Wine>> orders = new HashMap<>();
    private static final String FILE_NAME = "stock.txt";

    Random rand = new Random();

    @PostConstruct
    public void initData() {

        Wine a = new Wine();
        a.setName("Chardonnay");
        a.setYear(2020);
        a.setPrice(30.50);
        a.setPercentage(13.0f);
        a.setTastePallet(Aroma.ELDERFLOWER);
        a.setQuantity(10);
        wines.put(a.getName(), a);

        Wine b = new Wine();
        b.setName("Cabernet Sauvignon");
        b.setYear(2018);
        b.setPrice(45.99);
        b.setPercentage(13.5f);
        b.setTastePallet(Aroma.BLACKBERRY);
        b.setQuantity(5);
        wines.put(b.getName(), b);

        Wine c = new Wine();
        c.setName("Merlot");
        c.setYear(2017);
        c.setPrice(35.75);
        c.setPercentage(12.5f);
        c.setTastePallet(Aroma.PLUM);
        c.setQuantity(7);
        wines.put(c.getName(), c);

        Wine d = new Wine();
        d.setName("Pinot Noir");
        d.setYear(2019);
        d.setPrice(42.25);
        d.setPercentage(13.0f);
        d.setTastePallet(Aroma.CHERRY);
        d.setQuantity(3);
        wines.put(d.getName(), d);

        Wine e = new Wine();
        e.setName("Sauvignon Blanc");
        e.setYear(2021);
        e.setPrice(25.50);
        e.setPercentage(12.0f);
        e.setTastePallet(Aroma.CITRUS);
        e.setQuantity(8);
        wines.put(e.getName(), e);

        Wine f = new Wine();
        f.setName("Malbec");
        f.setYear(2016);
        f.setPrice(38.50);
        f.setPercentage(13.5f);
        f.setTastePallet(Aroma.BLACKBERRY);
        f.setQuantity(6);
        wines.put(f.getName(), f);

        Wine g = new Wine();
        g.setName("Syrah");
        g.setYear(2015);
        g.setPrice(40.50);
        g.setPercentage(13.5f);
        g.setTastePallet(Aroma.BLACKBERRY);
        g.setQuantity(4);
        wines.put(g.getName(), g);

        Wine h = new Wine();
        h.setName("Zinfandel");
        h.setYear(2014);
        h.setPrice(35.50);
        h.setPercentage(13.5f);
        h.setTastePallet(Aroma.BLACKBERRY);
        h.setQuantity(2);
        wines.put(h.getName(), h);

        Wine i = new Wine();
        i.setName("Riesling");
        i.setYear(2019);
        i.setPrice(28.50);
        i.setPercentage(12.0f);
        i.setTastePallet(Aroma.CITRUS);
        i.setQuantity(9);
        wines.put(i.getName(), i);

        Wine j = new Wine();
        j.setName("Gewurztraminer");
        j.setYear(2018);
        j.setPrice(32.50);
        j.setPercentage(12.5f);
        j.setTastePallet(Aroma.ROSE);
        j.setQuantity(2);
        wines.put(j.getName(), j);

        Wine k = new Wine();
        k.setName("Viognier");
        k.setYear(2017);
        k.setPrice(30.50);
        k.setPercentage(13.0f);
        k.setTastePallet(Aroma.JASMINE);
        k.setQuantity(3);
        wines.put(k.getName(), k);
    }

    // Write the stock to a file
    public void writeStock() {
        try {
            FileWriter writer = new FileWriter(FILE_NAME);
            for (Wine wine : wines.values()) {
                writer.write(wine.getName() + ": " + wine.getQuantity() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Look up the availability of wine with a given name
    public Wine findWine(String name) {
        Assert.notNull(name, "The wine's code must not be null");
        return wines.get(name);
    }

    // Search for the most expensive wine
    public Wine findMostExpensiveWine() {
        if (wines == null) return null;
        if (wines.size() == 0) return null;

        return wines.values().stream().max(Comparator.comparing(Wine::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    // Search for the cheapest wine
    public Wine findCheapestWine() {
        if (wines == null) return null;
        if (wines.size() == 0) return null;

        return wines.values().stream().min(Comparator.comparing(Wine::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public Order createOrder(List<String> wineList, String specialInstructions, String deliveryAddress) {

        Order order = new Order();

        order.setOrderNumber(rand.nextLong(99999) + 1L);
        order.setSpecialInstructions(specialInstructions);
        order.setTimestamp(LocalDateTime.now().toString());
        order.setDeliveryAddress(deliveryAddress);

        ArrayList<Wine> wineOrder = new ArrayList<Wine>();
        for (String wineName : wineList) {
            Wine wine = findWine(wineName);
            if (wine != null) {
                wineOrder.add(wine);
            } else {
                // Handle if meal name is not found
                System.out.println("Wine with name '" + wineName + "' not found. Wine has not been added to order.");
            }
        }

        // Add the order to the orders map
        orders.put(String.valueOf(order.getOrderNumber()), wineOrder);

        return order;
    }

    // Initialize the stock
    public void setWines(List<Wine> wines) {
        for (Wine wine : wines) {
            this.wines.put(wine.getName(), wine);
        }
    }

    // Add a wine to the stock
    public void addWine(Wine wine) {
        wines.put(wine.getName(), wine);
    }

    // Retrieve the stock
    public List<Wine> getWineCard() {
        return new ArrayList<Wine>(wines.keySet().stream().map(key -> wines.get(key)).collect(Collectors.toList()));
    }

    // Search for wines by aroma
    public List<Wine> getWineCardByAroma(Aroma aroma) {
        return wines.values().stream()
                .filter(wine -> wine.getTastePallet() == aroma)
                .collect(Collectors.toList());
    }
}