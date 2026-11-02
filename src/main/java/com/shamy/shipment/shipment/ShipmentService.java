package com.shamy.shipment.shipment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ShipmentDTO.ShipmentResponse createShipment(ShipmentDTO.CreateShipmentRequest request) {

        Shipment shipment = Shipment.builder()
                .trackingNumber(generateTrackingNumber())
                .origin(request.origin())
                .destination(request.destination())
                .estimatedDelivery(request.estimatedDelivery())
                .build();


        Shipment shipmentSaved = shipmentRepository.save(shipment);

        notifyShipmentStatus(shipmentSaved, getStatusMessage(shipmentSaved.getStatus()));

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
                        () -> new RuntimeException("Shipment with id: " + shipmentId + " does not exist")
                );
        return ShipmentDTO.toShipmentResponse(shipment);

    }

    public ShipmentDTO.ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(
                        () -> new RuntimeException("Shipment with trackingNumber: " + trackingNumber + " does not exist")
                );
        return ShipmentDTO.toShipmentResponse(shipment);
    }

    public ShipmentDTO.ShipmentResponse updateShipmentStatus(ShipmentDTO.UpdateShipmentRequest request, Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Shipment with id: " + id + " does not exist")
                );
        shipment.setStatus(request.status());
        shipment.setCurrentLocation(request.currentLocation());
        Shipment shipmentSaved = shipmentRepository.save(shipment);

        notifyShipmentStatus(shipmentSaved, getStatusMessage(shipmentSaved.getStatus()));

        return ShipmentDTO.toShipmentResponse(shipmentSaved);
    }

    public void notifyShipmentStatus(Shipment shipment, String message) {

        var update =
                ShipmentDTO.StatusUpdateMessage.builder()
                        .shipmentId(shipment.getId())
                        .trackingNumber(shipment.getTrackingNumber())
                        .status(shipment.getStatus())
                        .currentLocation(shipment.getCurrentLocation())
                        .timestamp(shipment.getUpdatedAt())
                        .message(message)
                        .build();

        messagingTemplate.convertAndSend("/topic/shipments/" + shipment.getId(), update);

        messagingTemplate.convertAndSend("/topic/shipments", update);

        log.info("Sent shipment status update {}", update);
    }

    private String getStatusMessage(ShipmentStatus status) {
        return switch (status) {
            case ORDER_PLACES -> "Order has been placed";
            case PROCESSING -> "Order has been processing";
            case PICKED_UP -> "Package has been picked up";
            case IN_TRANSIT -> "Package has been in transit";
            case OUT_FOR_DELIVERY -> "Package has been out-of-delivery";
            case DELIVERED -> "Package has been delivered";
            case EXCEPTION -> "Delivery has been failed";
        };
    }
}