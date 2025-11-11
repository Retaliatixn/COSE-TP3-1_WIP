package mos.controller;

import mos.model.Order;
import mos.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public CollectionModel<EntityModel<Order>> getAllOrders() {
        List<EntityModel<Order>> orders = orderService.getAllOrders().stream()
            .map(order -> EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(order.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(orders,
            linkTo(methodOn(OrderController.class).getAllOrders()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Order>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
            .map(order -> EntityModel.of(order,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Order>> createOrder(@RequestBody Order order) {
        Order savedOrder = orderService.createOrder(order);
        EntityModel<Order> orderResource = EntityModel.of(savedOrder,
            linkTo(methodOn(OrderController.class).getOrderById(savedOrder.getId())).withSelfRel(),
            linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
        
        return ResponseEntity.created(
            linkTo(methodOn(OrderController.class).getOrderById(savedOrder.getId())).toUri())
            .body(orderResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Order>> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        try {
            Order updatedOrder = orderService.updateOrder(id, orderDetails);
            EntityModel<Order> orderResource = EntityModel.of(updatedOrder,
                linkTo(methodOn(OrderController.class).getOrderById(id)).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
            
            return ResponseEntity.ok(orderResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
    
    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Order Service is working!";
    }
}