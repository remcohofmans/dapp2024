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


import io.foodmenu.gt.webservice.*;


import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
/*
test for commit
 */

@Component
public class MealRepository {
    private static final Map<String, Meal> meals = new HashMap<String, Meal>();
    private static final Map<String, ArrayList<Meal>> orders = new HashMap<>();

    Random rand = new Random();

    @PostConstruct
    public void initData() {

        Meal a = new Meal();
        a.setName("Steak");
        a.setDescription("Steak with fries");
        a.setMealtype(Mealtype.MEAT);
        a.setKcal(1100);
        a.setPrice(22.2);


        meals.put(a.getName(), a);

        Meal b = new Meal();
        b.setName("Portobello");
        b.setDescription("Portobello Mushroom Burger");
        b.setMealtype(Mealtype.VEGAN);
        b.setKcal(637);
        b.setPrice(15);


        meals.put(b.getName(), b);

        Meal c = new Meal();
        c.setName("Fish and Chips");
        c.setDescription("Fried fish with chips");
        c.setMealtype(Mealtype.FISH);
        c.setKcal(950);
        c.setPrice(30);

        meals.put(c.getName(), c);
    }

    public Meal findMeal(String name) {
        Assert.notNull(name, "The meal's code must not be null");
        return meals.get(name);
    }

    public Meal findBiggestMeal() {

        if (meals == null) return null; // The map is not initialized
        if (meals.size() == 0) return null; // The map is empty

        var values = meals.values();
        return values.stream().max(Comparator.comparing(Meal::getKcal)).orElseThrow(NoSuchElementException::new);
    }

    public Meal findCheapestMeal() {
        if (meals == null) return null;
        if (meals.size() == 0) return null;

        var values = meals.values();
        return values.stream().min(Comparator.comparing(Meal::getPrice)).orElseThrow(NoSuchElementException::new);
    }

    public Order createOrder(List<String> mealList, String specialInstructions, String deliveryAddress) {

        Order order = new Order();

        order.setOrderNumber(rand.nextLong(99999) + 1L);
        order.setSpecialInstructions(specialInstructions);
        order.setTimestamp(LocalDateTime.now().toString());
        order.setDeliveryAddress(deliveryAddress);

        ArrayList<Meal> mealOrder = new ArrayList<Meal>();
        for (String mealName : mealList) {
            Meal meal = findMeal(mealName);
            if (meal != null) {
                mealOrder.add(meal);
            } else {
                // Handle if meal name is not found
                System.out.println("Meal with name '" + mealName + "' not found. Meal has not been added to order.");
            }
        }

        // Add the order to the orders map
        orders.put(String.valueOf(order.getOrderNumber()), mealOrder);

        return order;
    }

    public void setMeals(List<Meal> meals) {
        for (Meal meal : meals) {
            this.meals.put(meal.getName(), meal);
        }
    }
}