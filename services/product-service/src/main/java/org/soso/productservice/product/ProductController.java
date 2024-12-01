package org.soso.productservice.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    ResponseEntity<Integer> createProduct(@RequestBody @Valid ProductRequest productRequest){
        return ResponseEntity.ok(productService.createProduct(productRequest));
    }
    @PostMapping("/purchase")
    ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(@RequestBody @Valid List<ProductPurchaseRequest> productRequest) {
        return ResponseEntity.ok(productService.purchaseProducts(productRequest)
        );
    }
    @GetMapping("/{productId}")
    ResponseEntity<ProductResponse> productsById(@PathVariable("productId") Integer id) {
        return ResponseEntity.ok(productService.productById(id)
        );
    }
    @GetMapping("/products")
    ResponseEntity<List<ProductResponse>> allProducts() {
        return ResponseEntity.ok(productService.allProducts());

    }
}
