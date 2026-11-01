package com.shamy.shipment.shipment;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.shamy.shipment.shipment.ShipmentDTO.*;

@RestController
@RequestMapping("/api/shipments")
@AllArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;


    @PostMapping
    public ResponseEntity<?> createShipment(@Valid @RequestBody CreateShipmentRequest request) {

        ShipmentDTO.ShipmentResponse shipment = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @GetMapping
    public ResponseEntity<?> getAllShipments() {
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShipmentById(@PathVariable Long id) {
        ShipmentDTO.ShipmentResponse response = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(response);
    }

}