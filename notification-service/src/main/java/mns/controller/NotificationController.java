package mns.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mns.model.Notification;
import mns.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Notification>> getNotification(@PathVariable Long id) {
        Notification notification = notificationService.getNotification(id);
        return ResponseEntity.ok(toModel(notification));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> getAllNotifications() {
        List<EntityModel<Notification>> notifications = notificationService.getAllNotifications()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Notification>> collectionModel = CollectionModel.of(notifications);
        collectionModel.add(linkTo(methodOn(NotificationController.class).getAllNotifications()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    private EntityModel<Notification> toModel(Notification notification) {
        EntityModel<Notification> model = EntityModel.of(notification);
        
        model.add(linkTo(methodOn(NotificationController.class).getNotification(notification.getId())).withSelfRel());
        model.add(linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications"));
        
        // Link to related resources
        if (notification.getOrderId() != null) {
            model.add(linkTo(methodOn(NotificationController.class).getNotification(notification.getId()))
                .slash("../../orders/" + notification.getOrderId())
                .withRel("order"));
        }
        if (notification.getCustomerId() != null) {
            model.add(linkTo(methodOn(NotificationController.class).getNotification(notification.getId()))
                .slash("../../customers/" + notification.getCustomerId())
                .withRel("customer"));
        }
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}