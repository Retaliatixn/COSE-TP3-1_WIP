package mss.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<EntityModel<Shipment>> getShipment(@PathVariable String id) {
        Shipment shipment = shippingService.getShipment(id);
        return ResponseEntity.ok(toModel(shipment));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CollectionModel<EntityModel<Shipment>>> getAllShipments() {
        List<EntityModel<Shipment>> shipments = shippingService.getAllShipments()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Shipment>> collectionModel = CollectionModel.of(shipments);
        collectionModel.add(linkTo(methodOn(ShippingController.class).getAllShipments()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<EntityModel<Shipment>> getShipmentByOrderId(@PathVariable Long orderId) {
        Shipment shipment = shippingService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(toModel(shipment));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Shipment>> createShipment(@RequestBody Shipment shipment) {
        Shipment created = shippingService.createShipment(shipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Shipment>> updateShipment(@PathVariable String id, @RequestBody Shipment shipment) {
        Shipment updated = shippingService.updateShipment(id, shipment);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShipment(@PathVariable String id) {
        shippingService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
    
    private EntityModel<Shipment> toModel(Shipment shipment) {
        EntityModel<Shipment> model = EntityModel.of(shipment);
        
        model.add(linkTo(methodOn(ShippingController.class).getShipment(shipment.getId())).withSelfRel());
        model.add(linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments"));
        
        // State-based links
        if ("PROCESSING".equals(shipment.getStatus()) || "PENDING".equals(shipment.getStatus())) {
            model.add(Link.of("/api/shipping/" + shipment.getId() + "/ship", "ship"));
        } else if ("SHIPPED".equals(shipment.getStatus())) {
            model.add(Link.of("/api/shipping/" + shipment.getId() + "/track", "track"));
        }
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}