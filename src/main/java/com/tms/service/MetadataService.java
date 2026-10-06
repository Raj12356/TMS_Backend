package com.tms.service;

import com.tms.entity.Category;
import com.tms.entity.Label;
import com.tms.repository.CategoryRepository;
import com.tms.repository.LabelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetadataService {

    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;

    public MetadataService(CategoryRepository categoryRepository, LabelRepository labelRepository) {
        this.categoryRepository = categoryRepository;
        this.labelRepository = labelRepository;
    }

    public List<String> getCategories() {
        return categoryRepository.findAllByOrderByIdAsc().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> replaceCategories(List<String> rawNames) {
        List<String> cleaned = cleanList(rawNames);
        categoryRepository.deleteAll();
        categoryRepository.flush();

        List<Category> entities = cleaned.stream()
                .map(Category::new)
                .collect(Collectors.toList());
        categoryRepository.saveAll(entities);

        return getCategories();
    }

    public List<String> getLabels() {
        return labelRepository.findAllByOrderByIdAsc().stream()
                .map(Label::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<String> replaceLabels(List<String> rawNames) {
        List<String> cleaned = cleanList(rawNames);
        labelRepository.deleteAll();
        labelRepository.flush();

        List<Label> entities = cleaned.stream()
                .map(Label::new)
                .collect(Collectors.toList());
        labelRepository.saveAll(entities);

        return getLabels();
    }

    private List<String> cleanList(List<String> input) {
        if (input == null) return Collections.emptyList();
        Set<String> seen = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (String item : input) {
            if (item == null) continue;
            String trimmed = item.trim();
            if (trimmed.isEmpty()) continue;
            if (trimmed.length() > 60) {
                trimmed = trimmed.substring(0, 60);
            }
            String lower = trimmed.toLowerCase();
            if (!seen.contains(lower)) {
                seen.add(lower);
                result.add(trimmed);
            }
            if (result.size() >= 200) break;
        }
        return result;
    }
}

