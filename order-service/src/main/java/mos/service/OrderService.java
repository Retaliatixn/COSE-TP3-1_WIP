package mos.service;

import mos.client.CustomerServiceClient;
import mos.client.InventoryServiceClient;
import mos.dto.OrderRequest;
import mos.events.OrderEvent;
import mos.model.Order;
import mos.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final CustomerServiceClient customerClient;
    private final InventoryServiceClient inventoryClient;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public OrderService(OrderRepository orderRepository,
                       CustomerServiceClient customerClient,
                       InventoryServiceClient inventoryClient,
                       KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for customer {} and product {}", 
                request.getCustomerId(), request.getProductId());
        
        // Step 1: Validate customer exists (SYNC)
        boolean customerExists = customerClient.customerExists(request.getCustomerId());
        if (!customerExists) {
            throw new RuntimeException("Customer not found: " + request.getCustomerId());
        }
        log.info("Customer {} validated", request.getCustomerId());
        
        // Step 2: Check inventory and get product info (SYNC)
        var productInfo = inventoryClient.checkInventory(
            request.getProductId(), 
            request.getQuantity()
        );
        if (!productInfo.isAvailable()) {
            throw new RuntimeException("Insufficient inventory for product: " + request.getProductId());
        }
        log.info("Inventory validated for product {}", request.getProductId());
        
        // Step 3: Create order
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(productInfo.getPrice() * request.getQuantity());
        order.setStatus(Order.OrderStatus.VALIDATED);
        
        order = orderRepository.save(order);
        log.info("Order {} created successfully", order.getId());
        
        // Step 4: Publish event ASYNCHRONOUSLY - don't wait for result
        publishOrderEvent(order);
        
        return order;
    }
    
    // Separate method that publishes async and doesn't throw exceptions
    private void publishOrderEvent(Order order) {
        try {
            OrderEvent event = new OrderEvent(
                order.getId(),
                order.getCustomerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalAmount(),
                order.getStatus().toString()
            );
            
            // Send async - add callback for success/failure
            kafkaTemplate.send("order-created", String.valueOf(order.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Order event published successfully for order {}", order.getId());
                    } else {
                        log.error("Failed to publish order event for order {}: {}", 
                                order.getId(), ex.getMessage());
                    }
                });
                
        } catch (Exception e) {
            log.error("Exception while publishing order event: {}", e.getMessage());
        }
    }
    
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}