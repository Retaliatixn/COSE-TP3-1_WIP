package mis.controller;

import mis.model.Product;
import mis.service.InventoryService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @PostMapping
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product product) {
        Product created = inventoryService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(created));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> getProduct(@PathVariable String id) {
        Product product = inventoryService.getProduct(id);
        return ResponseEntity.ok(toModel(product));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProducts() {
        List<EntityModel<Product>> products = inventoryService.getAllProducts()
            .stream()
            .map(this::toModel)
            .collect(Collectors.toList());
        
        CollectionModel<EntityModel<Product>> collectionModel = CollectionModel.of(products);
        collectionModel.add(linkTo(methodOn(InventoryController.class).getAllProducts()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable String id, @RequestBody Product product) {
        Product updated = inventoryService.updateProduct(id, product);
        return ResponseEntity.ok(toModel(updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/reserve")
    public ResponseEntity<EntityModel<Product>> reserveStock(@PathVariable String id, @RequestParam Integer quantity) {
        Product product = inventoryService.reserveStock(id, quantity);
        return ResponseEntity.ok(toModel(product));
    }
    
    private EntityModel<Product> toModel(Product product) {
        EntityModel<Product> model = EntityModel.of(product);
        
        model.add(linkTo(methodOn(InventoryController.class).getProduct(product.getId())).withSelfRel());
        model.add(linkTo(methodOn(InventoryController.class).getAllProducts()).withRel("products"));
        model.add(linkTo(methodOn(InventoryController.class).updateProduct(product.getId(), null))
            .withRel("update"));
        model.add(linkTo(methodOn(InventoryController.class).deleteProduct(product.getId()))
            .withRel("delete"));
        
        // Only show reserve link if there's stock
        if (product.getQuantity() > 0) {
            model.add(linkTo(methodOn(InventoryController.class).reserveStock(product.getId(), null))
                .withRel("reserve"));
        }
        
        return model;
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}