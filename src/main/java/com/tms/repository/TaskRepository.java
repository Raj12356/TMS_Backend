package com.tms.repository;

import com.tms.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByOrderByIdAsc();
    List<Task> findByUserIdOrderByIdAsc(Integer userId);
    List<Task> findByAssignedToOrderByIdAsc(Integer assignedTo);
    List<Task> findByUserIdAndAssignedToOrderByIdAsc(Integer userId, Integer assignedTo);
    void deleteByUserId(Integer userId);
}

