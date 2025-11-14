package mps.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;
import java.util.Date;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends RepresentationModel<Payment> {  // ← EXTENDS HATEOAS!
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderId;
    private Double amount;
    private String paymentMethod; // "CREDIT_CARD", "PAYPAL", "BANK_TRANSFER"
    private String status; // "PENDING", "COMPLETED", "FAILED", "REFUNDED"
    private String transactionId;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}