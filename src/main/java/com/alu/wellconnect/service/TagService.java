package com.alu.wellconnect.service;

import com.alu.wellconnect.dto.TagRequest;
import com.alu.wellconnect.entity.Tag;
import com.alu.wellconnect.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @CacheEvict(value = "tags", allEntries = true)
    public Tag createTag(TagRequest request) {
        Tag tag = Tag.builder()
                .name(request.getName())
                .build();
        return tagRepository.save(tag);
    }

    @Cacheable(value = "tags")
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
}
