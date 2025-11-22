package mis.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mis.model.Product;
import mis.repository.ProductRepository;

@Service
public class InventoryService {
    private final ProductRepository productRepository;
    
    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    public Product getProduct(String id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product updateProduct(String id, Product product) {
        Product existing = getProduct(id);
        existing.setName(product.getName());
        existing.setQuantity(product.getQuantity());
        existing.setPrice(product.getPrice());
        return productRepository.save(existing);
    }
    
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    
    public Product reserveStock(String id, Integer quantity) {
        Product product = getProduct(id);
        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + id);
        }
        product.setQuantity(product.getQuantity() - quantity);
        return productRepository.save(product);
    }
}