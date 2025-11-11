package mis.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mis.model.Product;
import mis.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping
    public CollectionModel<EntityModel<Product>> getAllProducts() {
        List<EntityModel<Product>> products = inventoryService.getAllProducts().stream()
            .map(product -> EntityModel.of(product,
                linkTo(methodOn(InventoryController.class).getProductById(product.getId())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).getAllProducts()).withRel("products")))
            .collect(Collectors.toList());
        
        return CollectionModel.of(products,
            linkTo(methodOn(InventoryController.class).getAllProducts()).withSelfRel());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> getProductById(@PathVariable String id) {
        return inventoryService.getProductById(id)
            .map(product -> EntityModel.of(product,
                linkTo(methodOn(InventoryController.class).getProductById(id)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).getAllProducts()).withRel("products")))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product product) {
        Product savedProduct = inventoryService.createProduct(product);
        EntityModel<Product> productResource = EntityModel.of(savedProduct,
            linkTo(methodOn(InventoryController.class).getProductById(savedProduct.getId())).withSelfRel(),
            linkTo(methodOn(InventoryController.class).getAllProducts()).withRel("products"));
        
        return ResponseEntity.created(
            linkTo(methodOn(InventoryController.class).getProductById(savedProduct.getId())).toUri())
            .body(productResource);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        try {
            Product updatedProduct = inventoryService.updateProduct(id, productDetails);
            EntityModel<Product> productResource = EntityModel.of(updatedProduct,
                linkTo(methodOn(InventoryController.class).getProductById(id)).withSelfRel(),
                linkTo(methodOn(InventoryController.class).getAllProducts()).withRel("products"));
            
            return ResponseEntity.ok(productResource);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/test")
    public String test() {
        return "Inventory Service is working!";
    }
}