package org.soso.productservice.product;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
    
import org.soso.productservice.product.exception.ProductPurchaseException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    public Integer createProduct(ProductRequest productRequest) {
        var product=productMapper.toProduct(productRequest);
        return  productRepository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
    //extract product ids
        var productIds=request.stream()//1,2,3
                .map(ProductPurchaseRequest::productId)
                .toList();
        var storedProducts=productRepository.findAllByIdInOrderById(productIds);//1,2
        if(productIds.size()!= storedProducts.size()){
            throw new ProductPurchaseException("one or more product does not exist");
        }
        var storesRequest=request
                .stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        var purchasedProduct=new ArrayList<ProductPurchaseResponse>();

        for(int i=0;i<storedProducts.size();i++){
            var product=storedProducts.get(i);
            var productRequest=storesRequest.get(i);
                if(product.getAvailableQuantity() < productRequest.quantity()){
                    throw new ProductPurchaseException("Insufficient stock quantity for product with ID:: " + productRequest.productId());
                }
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            productRepository.save(product);
            purchasedProduct.add(productMapper.toproductPurchaseResponse(product, productRequest.quantity()));
        }
        return purchasedProduct;
    }

    public ProductResponse productById(Integer id) {
      return   productRepository.findById(id).
              map(productMapper::toProductResponse).
              orElseThrow(
                      () -> new EntityNotFoundException("Product not found with ID:: " + id)
      );
    }

    public List<ProductResponse> allProducts() {
        return productRepository.findAll()
                .stream().map(productMapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
