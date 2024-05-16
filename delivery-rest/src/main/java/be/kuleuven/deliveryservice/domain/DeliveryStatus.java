package be.kuleuven.deliveryservice.domain;

public enum DeliveryStatus {
        UNAVAIALBLE,
        AVAILABLE,
        PROCESSING,
        DELIVERED,
        CANCELLED,
        UNABLE_TO_CALCULATE_PRICE_BECAUSE_OF_NETWORK_FAILURE_TO_ACCESS_GOOGLE_MAP_DISTANCE
}

