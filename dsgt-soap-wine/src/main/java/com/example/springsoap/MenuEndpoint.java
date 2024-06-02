package com.example.springsoap;

import io.winemenu.gt.webservice.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class MenuEndpoint {
    private static final String NAMESPACE_URI = "http://winemenu.io/gt/webservice";

    private WineRepository wineRepo;

    @Autowired
    public MenuEndpoint(WineRepository wineRepo) {
        this.wineRepo = wineRepo;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWineRequest")
    @ResponsePayload
    public GetWineResponse getWine(@RequestPayload GetWineRequest request) {
        GetWineResponse response = new GetWineResponse();
        response.setWine(wineRepo.findWine(request.getName()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCheapestWineRequest")
    @ResponsePayload
    public GetCheapestWineResponse getCheapestMeal(@RequestPayload GetCheapestWineRequest request) {
        GetCheapestWineResponse response = new GetCheapestWineResponse();
        response.setWine(wineRepo.findCheapestWine());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMostExpensiveWineRequest")
    @ResponsePayload
    public GetMostExpensiveWineResponse getMostExpensiveWine(@RequestPayload GetMostExpensiveWineRequest request) {
        GetMostExpensiveWineResponse response = new GetMostExpensiveWineResponse();
        response.setWine(wineRepo.findMostExpensiveWine());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        GetOrderResponse response = new GetOrderResponse();

        // Retrieve the order details from the repository based on the request
        System.out.println("request = " + request.getOrder().getWineName());

        Order order = wineRepo.createOrder(request.getOrder().getWineName(), request.getOrder().getSpecialInstructions(), request.getOrder().getDeliveryAddress());

        if (order != null) {
            response.setResponseText("Order placed successfully. Your order number is " + order.getOrderNumber() + ", and will be delivered to " + order.getDeliveryAddress() + " within 30 minutes.");
        } else {
            response.setResponseText("Sorry, your order could not be registered. Please try again later.");
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWineCardRequest")
    @ResponsePayload
    public GetWineCardResponse getMenu(@RequestPayload GetWineCardRequest request) {
        GetWineCardResponse response = new GetWineCardResponse();
        List<Wine> wines = wineRepo.getWineCard();
        response.getWine().addAll(wines);
        return response;
    }

//    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getWineCardByAromaRequest")
//    @ResponsePayload
//    public GetWineCardByAromaResponse getMenuByAroma(@RequestPayload GetWineCardByAromaRequest request) {
//        GetWineCardByAromaResponse response = new GetWineCardByAromaResponse();
//        List<Wine> wines = wineRepo.getWineCardByAroma(request.getAroma());
//        response.getWine().addAll(wines);
//        return response;
//    }
}