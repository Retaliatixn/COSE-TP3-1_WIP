package mcs.controller;

import mcs.model.Customer;
import mcs.service.CustomerService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;
    
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer created = customerService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Customer>> getCustomer(@PathVariable Long id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(toModel(customer));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Customer>>> getAllCustomers() {
        List<EntityModel<Customer>> customers = customerService.getAllCustomers()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Customer>> collectionModel = CollectionModel.of(customers);
        collectionModel.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Customer>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updated = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    private EntityModel<Customer> toModel(Customer customer) {
        EntityModel<Customer> model = EntityModel.of(customer);
        
        model.add(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel());
        model.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"));
        model.add(linkTo(methodOn(CustomerController.class).updateCustomer(customer.getId(), null))
            .withRel("update"));
        model.add(linkTo(methodOn(CustomerController.class).deleteCustomer(customer.getId()))
            .withRel("delete"));
        
        // Link to customer's orders
        model.add(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId()))
            .slash("orders")
            .withRel("orders"));
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}