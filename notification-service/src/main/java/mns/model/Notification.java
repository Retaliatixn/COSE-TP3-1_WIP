package mns.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import java.util.Date;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends RepresentationModel<Notification> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String recipient;    // email, phone, user ID
    private String message;      // notification content
    private String type;         // "EMAIL", "SMS", "PUSH", "ALERT"
    private String status;       // "SENT", "PENDING", "FAILED"
    private Date createdAt;
}