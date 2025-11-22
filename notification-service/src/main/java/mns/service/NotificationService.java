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
}