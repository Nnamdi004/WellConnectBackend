package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.CategoryRequest;
import com.alu.wellconnect.entity.StoryCategory;
import com.alu.wellconnect.repository.StoryCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final StoryCategoryRepository categoryRepository;

    public StoryCategory createCategory(CategoryRequest request) {
        StoryCategory category = StoryCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return categoryRepository.save(category);
    }

    public List<StoryCategory> getAllCategories() {
        return categoryRepository.findAll();
    }
}