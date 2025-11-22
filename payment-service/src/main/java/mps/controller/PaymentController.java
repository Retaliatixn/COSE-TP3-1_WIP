package mps.controller;

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

import mps.model.Payment;
import mps.service.PaymentService;

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
    
    @PostMapping
    public ResponseEntity<EntityModel<Payment>> createPayment(@RequestBody Payment payment) {
        Payment created = paymentService.createPayment(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Payment>> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        Payment updated = paymentService.updatePayment(id, payment);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    
    private EntityModel<Payment> toModel(Payment payment) {
        EntityModel<Payment> model = EntityModel.of(payment);
        
        model.add(linkTo(methodOn(PaymentController.class).getPayment(payment.getId())).withSelfRel());
        model.add(linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments"));
        model.add(linkTo(methodOn(PaymentController.class).getPaymentByOrderId(payment.getOrderId()))
            .withRel("payment-by-order"));
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}