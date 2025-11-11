package mis.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import mis.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
}