package com.example.springsoap;

import io.liquormenu.gt.webservice.*;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
public class MenuEndpoint {
    private static final String NAMESPACE_URI = "http://liquormenu.io/gt/webservice";

    private LiquorRepository liquorRepo;

    @Autowired
    public MenuEndpoint(LiquorRepository liquorRepo)
    {
        this.liquorRepo = liquorRepo;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLiquorRequest")
    @ResponsePayload
    public GetLiquorResponse getLiquor(@RequestPayload GetLiquorRequest request) {
        GetLiquorResponse response = new GetLiquorResponse();
        response.setLiquor(liquorRepo.findLiquor(request.getBrand()));

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCheapestLiquorRequest")
    @ResponsePayload
    public GetCheapestLiquorResponse getCheapestLiquor(@RequestPayload GetCheapestLiquorRequest request) {
        GetCheapestLiquorResponse response = new GetCheapestLiquorResponse();
        response.setLiquor(liquorRepo.findCheapestLiquor());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMostExpensiveLiquorRequest")
    @ResponsePayload
    public GetMostExpensiveLiquorResponse getMostExpensiveLiquor(@RequestPayload GetMostExpensiveLiquorRequest request) {
        GetMostExpensiveLiquorResponse response = new GetMostExpensiveLiquorResponse();
        response.setLiquor(liquorRepo.findMostExpensiveLiquor());

        return response;
    }
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        GetOrderResponse response = new GetOrderResponse();

        // Retrieve the order details from the repository based on the request
        System.out.println("request = " + request.getOrder().getLiquorBrand());

        Order order = liquorRepo.createOrder(request.getOrder().getLiquorBrand(), request.getOrder().getSpecialInstructions(), request.getOrder().getDeliveryAddress());

        if(order != null)  {
            response.setOrder(order);
            response.setResponseText("Order placed successfully. Your order number is " + order.getOrderNumber() + ", and will be delivered to " + order.getDeliveryAddress());
        }
        else {
            response.setResponseText("Sorry, your order could not be registered. Please try again later.");
        }

        return response;
    }
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getLiquorCardRequest")
    @ResponsePayload
    public GetLiquorCardResponse getMenu(@RequestPayload GetLiquorCardRequest request) {
        GetLiquorCardResponse response = new GetLiquorCardResponse();
        List<Liquor> liquors = liquorRepo.getLiquorCard();
        response.getLiquor().addAll(liquors);
        return response;
    }
}
