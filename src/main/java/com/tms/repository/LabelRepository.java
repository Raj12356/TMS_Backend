package com.tms.repository;

import com.tms.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Integer> {
    List<Label> findAllByOrderByIdAsc();
    boolean existsByNameIgnoreCase(String name);
}

