package mos.controller;

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

import mos.dto.OrderRequest;
import mos.model.Order;
import mos.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<EntityModel<Order>> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        EntityModel<Order> model = toModel(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<EntityModel<Order>> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(toModel(order));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CollectionModel<EntityModel<Order>>> getAllOrders() {
        List<EntityModel<Order>> orders = orderService.getAllOrders()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Order>> collectionModel = CollectionModel.of(orders);
        collectionModel.add(linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Order>> updateOrder(@PathVariable Long id, @RequestBody OrderRequest request) {
        Order order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(toModel(order));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    // Convert Order to HATEOAS EntityModel with links based on state
    private EntityModel<Order> toModel(Order order) {
        EntityModel<Order> model = EntityModel.of(order);
        
        // Self link - always present
        model.add(linkTo(methodOn(OrderController.class).getOrder(order.getId()))
            .withSelfRel());
        
        // State-based links - only show actions that are valid for current state
        switch (order.getStatus()) {
            case PENDING, VALIDATED -> 
                // Can cancel pending or validated orders
                model.add(Link.of("/api/orders/" + order.getId() + "/cancel", "cancel"));
                
            case PAYMENT_PROCESSING -> 
                // Show payment status link
                model.add(Link.of("/api/payments?orderId=" + order.getId(), "payment-status"));
                
            case PAID -> 
                // Can request shipment
                model.add(Link.of("/api/shipping?orderId=" + order.getId(), "request-shipment"));
                
            case SHIPPED -> 
                // Can track shipment
                model.add(Link.of("/api/orders/" + order.getId() + "/tracking", "track-shipment"));
                
            case DELIVERED -> 
                // Can request return
                model.add(Link.of("/api/orders/" + order.getId() + "/return", "request-return"));
                
            default -> {
                // For CANCELLED and FAILED, no additional actions
            }
        }
        
        // Links to related resources - always present
        model.add(Link.of("/api/customers/" + order.getCustomerId(), "customer"));
        model.add(Link.of("/api/inventory/" + order.getProductId(), "product"));
        
        return model;
    }
    
    // Exception handler
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}