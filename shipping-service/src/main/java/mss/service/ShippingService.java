package mss.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import mss.model.Shipment;
import mss.repository.ShippingRepository;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
    
    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
    
    public Shipment createShipment(Shipment shipment) {
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipment.setShippedDate(LocalDateTime.now());
        return shippingRepository.save(shipment);
    }
    
    public Shipment getShipment(String id) {
        return shippingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found: " + id));
    }
    
    public List<Shipment> getAllShipments() {
        return shippingRepository.findAll();
    }
    
    public Shipment updateShipment(String id, Shipment shipment) {
        Shipment existing = getShipment(id);
        existing.setStatus(shipment.getStatus());
        existing.setAddress(shipment.getAddress());
        return shippingRepository.save(existing);
    }
    
    public void deleteShipment(String id) {
        shippingRepository.deleteById(id);
    }
}