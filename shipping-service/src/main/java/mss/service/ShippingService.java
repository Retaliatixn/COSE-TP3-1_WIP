package mss.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mss.model.Shipment;
import mss.repository.ShippingRepository;

@Service
public class ShippingService {
    
    @Autowired
    private ShippingRepository shippingRepository;
    
    public List<Shipment> getAllShipments() {
        return shippingRepository.findAll();
    }
    
    public Optional<Shipment> getShipmentById(String id) {
        return shippingRepository.findById(id);
    }
    
    public Shipment createShipment(Shipment shipment) {
        // Set createdAt timestamp automatically
        shipment.setCreatedAt(new Date());
        return shippingRepository.save(shipment);
    }
    
    public Shipment updateShipment(String id, Shipment shipmentDetails) {
        Shipment shipment = shippingRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shipment not found with id: " + id));
        
        // ONLY UPDATE FIELDS THAT ACTUALLY EXIST IN YOUR SHIPMENT CLASS:
        shipment.setOrderId(shipmentDetails.getOrderId());
        shipment.setTrackingNumber(shipmentDetails.getTrackingNumber());
        shipment.setStatus(shipmentDetails.getStatus());
        // Don't set createdAt on update - keep original creation date
        
        return shippingRepository.save(shipment);
    }
    
    public void deleteShipment(String id) {
        shippingRepository.deleteById(id);
    }
}