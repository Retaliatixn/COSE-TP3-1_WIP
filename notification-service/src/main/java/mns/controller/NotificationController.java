package mns.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<EntityModel<Notification>> getNotification(@PathVariable Long id) {
        Notification notification = notificationService.getNotification(id);
        return ResponseEntity.ok(toModel(notification));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> getAllNotifications() {
        List<EntityModel<Notification>> notifications = notificationService.getAllNotifications()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Notification>> collectionModel = CollectionModel.of(notifications);
        collectionModel.add(linkTo(methodOn(NotificationController.class).getAllNotifications()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> getNotificationsByOrderId(@PathVariable Long orderId) {
        List<EntityModel<Notification>> notifications = notificationService.getNotificationsByOrderId(orderId)
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Notification>> collectionModel = CollectionModel.of(notifications);
        collectionModel.add(linkTo(methodOn(NotificationController.class).getNotificationsByOrderId(orderId)).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<CollectionModel<EntityModel<Notification>>> getNotificationsByCustomerId(@PathVariable Long customerId) {
        List<EntityModel<Notification>> notifications = notificationService.getNotificationsByCustomerId(customerId)
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Notification>> collectionModel = CollectionModel.of(notifications);
        collectionModel.add(linkTo(methodOn(NotificationController.class).getNotificationsByCustomerId(customerId)).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Notification>> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EntityModel<Notification>> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        Notification updated = notificationService.updateNotification(id, notification);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    private EntityModel<Notification> toModel(Notification notification) {
        return EntityModel.of(notification,
            linkTo(methodOn(NotificationController.class).getNotification(notification.getId())).withSelfRel(),
            linkTo(methodOn(NotificationController.class).getAllNotifications()).withRel("notifications"),
            linkTo(methodOn(NotificationController.class).getNotificationsByOrderId(notification.getOrderId())).withRel("order-notifications"),
            linkTo(methodOn(NotificationController.class).getNotificationsByCustomerId(notification.getCustomerId())).withRel("customer-notifications"));
    }
}