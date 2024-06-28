package com.diogoazevedo.challenge_anota_ai.services;

import com.diogoazevedo.challenge_anota_ai.domain.category.Category;
import com.diogoazevedo.challenge_anota_ai.domain.category.CategoryDTO;
import com.diogoazevedo.challenge_anota_ai.domain.category.exceptions.CategoryNotFoundException;
import com.diogoazevedo.challenge_anota_ai.repositories.CategoryRepository;
import com.diogoazevedo.challenge_anota_ai.services.aws.AwsSnsService;
import com.diogoazevedo.challenge_anota_ai.services.aws.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AwsSnsService snsService;

    public CategoryService(
            CategoryRepository categoryRepository,
            AwsSnsService snsService
    ) {
        this.categoryRepository = categoryRepository;
        this.snsService = snsService;
    }

    public Category create(CategoryDTO categoryData) {
        Category newCategory = new Category(categoryData);
        this.categoryRepository.save(newCategory);
        this.snsService.publish(new MessageDTO(newCategory.toString()));
        return newCategory;
    }

    public Optional<Category> getById(String id) {
        return this.categoryRepository.findById(id);
    }

    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    public Category update(String id, CategoryDTO categoryData) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        if (!categoryData.title().isEmpty()) category.setTitle(categoryData.title());
        if (!categoryData.description().isEmpty()) category.setDescription(categoryData.description());
        this.categoryRepository.save(category);
        this.snsService.publish(new MessageDTO(category.toString()));
        return category;
    }

    public void delete(String id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        this.categoryRepository.delete(category);
    }
}
