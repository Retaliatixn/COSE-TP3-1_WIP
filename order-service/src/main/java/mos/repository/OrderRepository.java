package mos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mos.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerUsername(String username);
    // Basic CRUD operations are provided by JpaRepository
    // You can add custom query methods here later
}