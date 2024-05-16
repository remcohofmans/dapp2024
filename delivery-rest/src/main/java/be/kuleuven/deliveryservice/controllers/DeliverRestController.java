package be.kuleuven.deliveryservice.controllers;

import be.kuleuven.deliveryservice.domain.Deliver;
import be.kuleuven.deliveryservice.domain.DeliveryRepository;
import be.kuleuven.deliveryservice.domain.DeliveryStatus;
import be.kuleuven.deliveryservice.domain.GoogleCloudMapHelper;
import be.kuleuven.deliveryservice.exceptions.MealNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DeliverRestController {

    private final DeliveryRepository deliveryRepository;

    @Autowired
    DeliverRestController(DeliveryRepository orderRepository) {
        this.deliveryRepository = orderRepository;
    }



    @GetMapping("/rest/delivery/{id}")
    EntityModel<Deliver> getDeliveryById(@PathVariable String id) {
        Deliver deliver = deliveryRepository.findOrder(id).orElseThrow(() -> new MealNotFoundException(String.valueOf(id)));

        return deliveryToEntityModel(id, deliver);
    }



    @GetMapping("/rest/overviewOfAlldelivery")
    CollectionModel<EntityModel<Deliver> > getDeliveries() {

        //GoogleCloudMapHelper g = new GoogleCloudMapHelper();


        Collection<Deliver> deliveries = deliveryRepository.getAllDelivries();

        Collection<EntityModel<Deliver> > deliveryEntityModels = new ArrayList<>();
        for (Deliver o : deliveries) {
            EntityModel<Deliver> em = deliveryToEntityModel(o.getDeliveryID(), o);
            deliveryEntityModels.add(em);
        }
        return CollectionModel.of(deliveryEntityModels,
                linkTo(methodOn(DeliverRestController.class).getDeliveries()).withSelfRel());


        //return orderRepository.getAllOrders();
    }

    @PostMapping("/rest/delivery/{id}/{coordination}")
    public void addDelivery(@PathVariable String id, @PathVariable String coordination){
        deliveryRepository.addDelivery(id, coordination);
    }

    @PutMapping("/rest/delivery/{id}/{deliveryStatus}")
    public void updateOrder(@PathVariable String id, @PathVariable DeliveryStatus deliveryStatus){

        deliveryRepository.updateDeliveryStatus(id, deliveryStatus);
        //mealsRepository.updateMeal(id, meal);
    }

    @DeleteMapping("/rest/delivery/{id}")
        public void deleteOrder(@PathVariable String id){

        deliveryRepository.deleteDelivery(id);
    }


    private EntityModel<Deliver> deliveryToEntityModel(String id, Deliver odeliver) {
        return EntityModel.of(odeliver,
                linkTo(methodOn(DeliverRestController.class).getDeliveryById(id)).withSelfRel(),
                linkTo(methodOn(DeliverRestController.class).getDeliveries()).withRel("rest/delivery"));
    }



}
