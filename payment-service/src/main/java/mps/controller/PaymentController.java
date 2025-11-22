package mps.controller;

import mps.model.Payment;
import mps.service.PaymentService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Payment>> getPayment(@PathVariable Long id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(toModel(payment));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Payment>>> getAllPayments() {
        List<EntityModel<Payment>> payments = paymentService.getAllPayments()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Payment>> collectionModel = CollectionModel.of(payments);
        collectionModel.add(linkTo(methodOn(PaymentController.class).getAllPayments()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<EntityModel<Payment>> getPaymentByOrderId(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(toModel(payment));
    }
    
    private EntityModel<Payment> toModel(Payment payment) {
        EntityModel<Payment> model = EntityModel.of(payment);
        
        model.add(linkTo(methodOn(PaymentController.class).getPayment(payment.getId())).withSelfRel());
        model.add(linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments"));
        
        // Link to related order
        model.add(linkTo(methodOn(PaymentController.class).getPayment(payment.getId()))
            .slash("../../orders/" + payment.getOrderId())
            .withRel("order"));
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}