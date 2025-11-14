package mps.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mps.model.Payment;
import mps.repository.PaymentRepository;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment updatePayment(Long id, Payment paymentDetails) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        payment.setId(paymentDetails.getId());
        payment.setOrderId(paymentDetails.getOrderId());
        payment.setAmount(paymentDetails.getAmount());
        payment.setStatus(paymentDetails.getStatus());
        
        return paymentRepository.save(payment);
    }
    
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}