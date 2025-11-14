package mps.controller;

import mps.model.Payment;
import mps.service.PaymentService;
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
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public CollectionModel<EntityModel<Payment>> getAllPayments() {
        List<EntityModel<Payment>> payments = paymentService.getAllPayments().stream()
            .map(payment -> EntityModel.of(payment,
                linkTo(methodOn(PaymentController.class).getPaymentById(payment.getId())).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(payments,
            linkTo(methodOn(PaymentController.class).getAllPayments()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Payment>> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
            .map(payment -> EntityModel.of(payment,
                linkTo(methodOn(PaymentController.class).getPaymentById(id)).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Payment>> createPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment);
        EntityModel<Payment> paymentResource = EntityModel.of(savedPayment,
            linkTo(methodOn(PaymentController.class).getPaymentById(savedPayment.getId())).withSelfRel(),
            linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments"));
        
        return ResponseEntity.created(
            linkTo(methodOn(PaymentController.class).getPaymentById(savedPayment.getId())).toUri())
            .body(paymentResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Payment>> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id, paymentDetails);
            EntityModel<Payment> paymentResource = EntityModel.of(updatedPayment,
                linkTo(methodOn(PaymentController.class).getPaymentById(id)).withSelfRel(),
                linkTo(methodOn(PaymentController.class).getAllPayments()).withRel("payments"));
            
            return ResponseEntity.ok(paymentResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    
    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "Payment Service is working!";
    }
}