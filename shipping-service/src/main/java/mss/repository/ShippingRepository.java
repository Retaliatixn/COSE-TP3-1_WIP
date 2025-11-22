package mss.repository;

import mss.model.Shipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingRepository extends MongoRepository<Shipment, String> {
    Optional<Shipment> findByOrderId(Long orderId);
}