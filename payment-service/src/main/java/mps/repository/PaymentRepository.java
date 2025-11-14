package mps.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mps.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {  // Long, not String!
}