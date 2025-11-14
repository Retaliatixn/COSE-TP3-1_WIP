package mss.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import mss.model.Shipment;

@Repository
public interface ShippingRepository extends MongoRepository<Shipment, String> {
}