package com.shamy.shipment.shipment;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;


public class ShipmentDTO {
    public static record CreateShipmentRequest(
            @NotBlank(message = "Origin is required")
            String origin,

            @NotBlank(message = "Destination is required")
            String destination,

            @NotBlank(message = "Estimated Delivery is required")
            String estimatedDelivery
    ) {
    }

    public static record ShipmentResponse(
            Long id,
            String TrackingNumber,
            String origin,
            String destination,
            ShipmentStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String currenLocation,
            String estimatedDelivery

    ) {

    }

    public static ShipmentResponse toShipmentResponse(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getId(),
                shipment.getTrackingNumber(),
                shipment.getOrigin(),
                shipment.getDestination(),
                shipment.getStatus(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt(),
                shipment.getCurrentLocation(),
                shipment.getEstimatedDelivery()
        );
    }

}