package com.example.springsoap;

import io.foodmenu.gt.webservice.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class MenuEndpoint {
    private static final String NAMESPACE_URI = "http://foodmenu.io/gt/webservice";

    private MealRepository mealrepo;

    @Autowired
    public MenuEndpoint(MealRepository mealrepo) {
        this.mealrepo = mealrepo;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMealRequest")
    @ResponsePayload
    public GetMealResponse getMeal(@RequestPayload GetMealRequest request) {
        GetMealResponse response = new GetMealResponse();
        response.setMeal(mealrepo.findMeal(request.getName()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLargestMealRequest")
    @ResponsePayload
    public GetLargestMealResponse getLargestMeal(@RequestPayload GetLargestMealRequest request) {
        GetLargestMealResponse response = new GetLargestMealResponse();
        response.setMeal(mealrepo.findBiggestMeal());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCheapestMealRequest")
    @ResponsePayload
    public GetCheapestMealResponse getCheapestMeal(@RequestPayload GetCheapestMealRequest request) {
        GetCheapestMealResponse response = new GetCheapestMealResponse();
        response.setMeal(mealrepo.findCheapestMeal());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        GetOrderResponse response = new GetOrderResponse();

        // Retrieve the order details from the repository based on the request
        System.out.println("request = " + request.getOrder().getMealName());

        Order order = mealrepo.createOrder(request.getOrder().getMealName(), request.getOrder().getSpecialInstructions(), request.getOrder().getDeliveryAddress());

        if(order != null)  {
            response.setResponseText("Order placed successfully. Your order number is " + order.getOrderNumber() + ", and will be delivered to " + order.getDeliveryAddress() + " within 30 minutes.");
        }
        else {
            response.setResponseText("Sorry, your order could not be registered. Please try again later.");
        }

        return response;
    }
}