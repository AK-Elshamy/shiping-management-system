package com.shamy.shipment.shipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public ShipmentDTO.ShipmentResponse createShipment(ShipmentDTO.CreateShipmentRequest request) {

        Shipment shipment = Shipment.builder()
                .trackingNumber(generateTrackingNumber())
                .origin(request.origin())
                .destination(request.destination())
                .estimatedDelivery(request.estimatedDelivery())
                .build();


        Shipment shipmentSaved = shipmentRepository.save(shipment);
        return ShipmentDTO.toShipmentResponse(shipmentSaved);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public List<ShipmentDTO.ShipmentResponse> getAllShipments() {
        return shipmentRepository.findAll().stream().map(ShipmentDTO::toShipmentResponse).toList();
    }

    public ShipmentDTO.ShipmentResponse getShipmentById(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Shipment with id: " + shipmentId + " does not exist")
                );
        return ShipmentDTO.toShipmentResponse(shipment);

    }
}