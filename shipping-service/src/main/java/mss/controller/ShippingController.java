package mss.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mss.model.Shipment;
import mss.service.ShippingService;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingService shippingService;
    
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Shipment>> createShipment(@RequestBody Shipment shipment) {
        Shipment created = shippingService.createShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Shipment>> getShipment(@PathVariable String id) {
        Shipment shipment = shippingService.getShipment(id);
        return ResponseEntity.ok(toModel(shipment));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Shipment>>> getAllShipments() {
        List<EntityModel<Shipment>> shipments = shippingService.getAllShipments()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Shipment>> collectionModel = CollectionModel.of(shipments);
        collectionModel.add(linkTo(methodOn(ShippingController.class).getAllShipments()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Shipment>> updateShipment(@PathVariable String id, @RequestBody Shipment shipment) {
        Shipment updated = shippingService.updateShipment(id, shipment);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable String id) {
        shippingService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
    
    private EntityModel<Shipment> toModel(Shipment shipment) {
        EntityModel<Shipment> model = EntityModel.of(shipment);
        
        model.add(linkTo(methodOn(ShippingController.class).getShipment(shipment.getId())).withSelfRel());
        model.add(linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments"));
        
        // State-based links
        if ("PENDING".equals(shipment.getStatus())) {
            model.add(linkTo(methodOn(ShippingController.class).updateShipment(shipment.getId(), null))
                .withRel("ship"));
        } else if ("SHIPPED".equals(shipment.getStatus())) {
            model.add(linkTo(methodOn(ShippingController.class).getShipment(shipment.getId()))
                .slash("tracking")
                .withRel("track"));
        }
        
        // Link to related order
        model.add(linkTo(methodOn(ShippingController.class).getShipment(shipment.getId()))
            .slash("../../orders/" + shipment.getOrderId())
            .withRel("order"));
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}