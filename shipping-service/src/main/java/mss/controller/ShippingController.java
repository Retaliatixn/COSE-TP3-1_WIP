package mss.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    
    @Autowired
    private ShippingService shippingService;
    
    @GetMapping
    public CollectionModel<EntityModel<Shipment>> getAllShipments() {
        List<EntityModel<Shipment>> shipments = shippingService.getAllShipments().stream()
            .map(shipment -> EntityModel.of(shipment,
                linkTo(methodOn(ShippingController.class).getShipmentById(shipment.getId())).withSelfRel(),
                linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(shipments,
            linkTo(methodOn(ShippingController.class).getAllShipments()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Shipment>> getShipmentById(@PathVariable String id) {
        return shippingService.getShipmentById(id)
            .map(shipment -> EntityModel.of(shipment,
                linkTo(methodOn(ShippingController.class).getShipmentById(id)).withSelfRel(),
                linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Shipment>> createShipment(@RequestBody Shipment shipment) {
        Shipment savedShipment = shippingService.createShipment(shipment);
        EntityModel<Shipment> shipmentResource = EntityModel.of(savedShipment,
            linkTo(methodOn(ShippingController.class).getShipmentById(savedShipment.getId())).withSelfRel(),
            linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments"));
        
        return ResponseEntity.created(
            linkTo(methodOn(ShippingController.class).getShipmentById(savedShipment.getId())).toUri())
            .body(shipmentResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Shipment>> updateShipment(@PathVariable String id, @RequestBody Shipment shipmentDetails) {
        try {
            Shipment updatedShipment = shippingService.updateShipment(id, shipmentDetails);
            EntityModel<Shipment> shipmentResource = EntityModel.of(updatedShipment,
                linkTo(methodOn(ShippingController.class).getShipmentById(id)).withSelfRel(),
                linkTo(methodOn(ShippingController.class).getAllShipments()).withRel("shipments"));
            
            return ResponseEntity.ok(shipmentResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShipment(@PathVariable String id) {
        shippingService.deleteShipment(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    public String test() {
        return "Shipping Service is working!";
    }
}