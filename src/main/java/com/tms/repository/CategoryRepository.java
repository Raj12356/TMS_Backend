package com.tms.repository;

import com.tms.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByOrderByIdAsc();
    boolean existsByNameIgnoreCase(String name);
}

