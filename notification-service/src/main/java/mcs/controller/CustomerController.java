package mcs.controller;

import mcs.model.Customer;
import mcs.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public CollectionModel<EntityModel<Customer>> getAllCustomers() {
        List<EntityModel<Customer>> customers = customerService.getAllCustomers().stream()
            .map(customer -> EntityModel.of(customer,
                linkTo(methodOn(CustomerController.class).getCustomerById(customer.getId())).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(customers,
            linkTo(methodOn(CustomerController.class).getAllCustomers()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Customer>> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
            .map(customer -> EntityModel.of(customer,
                linkTo(methodOn(CustomerController.class).getCustomerById(id)).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.createCustomer(customer);
        EntityModel<Customer> customerResource = EntityModel.of(savedCustomer,
            linkTo(methodOn(CustomerController.class).getCustomerById(savedCustomer.getId())).withSelfRel(),
            linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"));
        
        return ResponseEntity.created(
            linkTo(methodOn(CustomerController.class).getCustomerById(savedCustomer.getId())).toUri())
            .body(customerResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Customer>> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customerDetails);
            EntityModel<Customer> customerResource = EntityModel.of(updatedCustomer,
                linkTo(methodOn(CustomerController.class).getCustomerById(id)).withSelfRel(),
                linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"));
            
            return ResponseEntity.ok(customerResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    public String test() {
        return "Customer Service is working!";
    }
}
