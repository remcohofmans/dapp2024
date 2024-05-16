package be.kuleuven.deliveryservice.exceptions;

public class MealNotFoundException extends RuntimeException {

    public MealNotFoundException(String id) {
        super("Could not find meal " + id);
    }
}
