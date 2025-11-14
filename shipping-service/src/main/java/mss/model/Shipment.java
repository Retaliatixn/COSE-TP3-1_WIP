package mss.model;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;  // ← ADD THIS!
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "shipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment extends RepresentationModel<Shipment> {  // ← EXTENDS HATEOAS!
    @Id
    private String id;
    private String orderId;
    private String trackingNumber;
    private String status;
    private Date createdAt;
}