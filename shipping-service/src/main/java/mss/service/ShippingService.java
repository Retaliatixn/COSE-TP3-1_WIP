package mss.service;

import mss.model.Shipment;
import mss.repository.ShippingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingService {
    private final ShippingRepository shippingRepository;
    
    public ShippingService(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
    
    public Shipment createShipment(Shipment shipment) {
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
    
    public Shipment getShipmentByOrderId(Long orderId) {
        return shippingRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));
    }
}