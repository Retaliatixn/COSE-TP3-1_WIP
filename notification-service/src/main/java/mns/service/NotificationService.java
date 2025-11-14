package mns.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mns.model.Notification;
import mns.repository.NotificationRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    public Notification createNotification(Notification notification) {
        notification.setCreatedAt(new Date());
        return notificationRepository.save(notification);
    }
    
    public Notification updateNotification(Long id, Notification notificationDetails) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        
        notification.setRecipient(notificationDetails.getRecipient());
        notification.setMessage(notificationDetails.getMessage());
        notification.setType(notificationDetails.getType());
        notification.setStatus(notificationDetails.getStatus());
        
        return notificationRepository.save(notification);
    }
    
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
}
