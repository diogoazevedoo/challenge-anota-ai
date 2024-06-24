package com.diogoazevedo.challenge_anota_ai.services;

import com.diogoazevedo.challenge_anota_ai.domain.category.Category;
import com.diogoazevedo.challenge_anota_ai.domain.category.CategoryDTO;
import com.diogoazevedo.challenge_anota_ai.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(CategoryDTO categoryData) {
        Category newCategory = new Category(categoryData);
        this.categoryRepository.save(newCategory);
        return newCategory;
    }
}
