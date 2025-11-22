package mns.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mns.model.Notification;
import mns.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    public Notification getNotification(Long id) {
        return notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
    }
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public List<Notification> getNotificationsByOrderId(Long orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
    
    public List<Notification> getNotificationsByCustomerId(Long customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }
    
    public Notification updateNotification(Long id, Notification notification) {
        Notification existing = getNotification(id);
        if (notification.getMessage() != null) {
            existing.setMessage(notification.getMessage());
        }
        if (notification.getStatus() != null) {
            existing.setStatus(notification.getStatus());
        }
        if (notification.getType() != null) {
            existing.setType(notification.getType());
        }
        if (notification.getChannel() != null) {
            existing.setChannel(notification.getChannel());
        }
        return notificationRepository.save(existing);
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}