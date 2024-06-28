package com.diogoazevedo.challenge_anota_ai.services;

import com.diogoazevedo.challenge_anota_ai.domain.category.Category;
import com.diogoazevedo.challenge_anota_ai.domain.category.exceptions.CategoryNotFoundException;
import com.diogoazevedo.challenge_anota_ai.domain.product.Product;
import com.diogoazevedo.challenge_anota_ai.domain.product.ProductDTO;
import com.diogoazevedo.challenge_anota_ai.domain.product.exceptions.ProductNotFoundException;
import com.diogoazevedo.challenge_anota_ai.repositories.ProductRepository;
import com.diogoazevedo.challenge_anota_ai.services.aws.AwsSnsService;
import com.diogoazevedo.challenge_anota_ai.services.aws.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final AwsSnsService snsService;

    public ProductService(
            ProductRepository productRepository,
            CategoryService categoryService,
            AwsSnsService snsService
    ) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.snsService = snsService;
    }

    public Product create(ProductDTO productData) {
        this.categoryService.getById(productData.categoryId())
                .orElseThrow(CategoryNotFoundException::new);
        Product newProduct = new Product(productData);
        this.productRepository.save(newProduct);
        this.snsService.publish(new MessageDTO(newProduct.toString()));
        return newProduct;
    }

    public List<Product> getAll() {
        return this.productRepository.findAll();
    }

    public Product update(String id, ProductDTO productData) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        if (productData.categoryId() != null) {
            this.categoryService.getById(productData.categoryId())
                    .orElseThrow(CategoryNotFoundException::new);
            product.setCategoryId(productData.categoryId());
        }
        if (!productData.title().isEmpty()) product.setTitle(productData.title());
        if (!productData.description().isEmpty()) product.setDescription(productData.description());
        if (!(productData.price() == null))  product.setPrice(productData.price());
        this.productRepository.save(product);
        this.snsService.publish(new MessageDTO(product.toString()));
        return product;
    }

    public void delete(String id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
        this.productRepository.delete(product);
    }
}
