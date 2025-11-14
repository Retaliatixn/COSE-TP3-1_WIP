package mns.controller;

import mns.model.Notification;
import mns.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public CollectionModel<EntityModel<Notification>> getAllNotifications() {
        List<EntityModel<Notification>> notifications = notificationService.getAllNotifications().stream()
            .map(notification -> EntityModel.of(notification,
                linkTo(methodOn(NotificationController.class).getNotificationById(notification.getId())).withSelfRel(),
                linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(notifications,
            linkTo(methodOn(NotificationController.class).getAllNotifications()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notification>> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
            .map(notification -> EntityModel.of(notification,
                linkTo(methodOn(NotificationController.class).getNotificationById(id)).withSelfRel(),
                linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Notification>> createNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.createNotification(notification);
        EntityModel<Notification> notificationResource = EntityModel.of(savedNotification,
            linkTo(methodOn(NotificationController.class).getNotificationById(savedNotification.getId())).withSelfRel(),
            linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications"));
        
        return ResponseEntity.created(
            linkTo(methodOn(NotificationController.class).getNotificationById(savedNotification.getId())).toUri())
            .body(notificationResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Notification>> updateNotification(@PathVariable Long id, @RequestBody Notification notificationDetails) {
        try {
            Notification updatedNotification = notificationService.updateNotification(id, notificationDetails);
            EntityModel<Notification> notificationResource = EntityModel.of(updatedNotification,
                linkTo(methodOn(NotificationController.class).getNotificationById(id)).withSelfRel(),
                linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications"));
            
            return ResponseEntity.ok(notificationResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    public String test() {
        return "Notification Service is working!";
    }
}
