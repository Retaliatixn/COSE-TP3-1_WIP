package mis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.RepresentationModel;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends RepresentationModel<Product> {
    @Id
    private String id;
    private String name;
    private String description;
    private Integer quantity;
    private Double price;
    private String category;
}