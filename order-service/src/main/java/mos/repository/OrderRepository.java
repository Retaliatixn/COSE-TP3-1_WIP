package mos.repository;

import mos.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Basic CRUD operations are provided by JpaRepository
    // You can add custom query methods here later
}