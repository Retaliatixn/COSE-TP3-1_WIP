package mcs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mcs.model.Customer;
import mcs.repository.CustomerRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found : " + id));
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existing = getCustomer(id);
        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setEmail(customer.getEmail());
        return customerRepository.save(existing);
    }
    
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}