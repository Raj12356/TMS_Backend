package com.tms;

import com.tms.dto.CommentDto;
import com.tms.entity.Task;
import com.tms.repository.TaskRepository;
import com.tms.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testCommentLifecycle() {
        List<Task> tasks = taskRepository.findAll();
        assertFalse(tasks.isEmpty(), "Tasks should exist");

        Task task = tasks.get(0);
        Long taskId = task.getId();

        // 1. Test get comments
        List<CommentDto> commentsBefore = commentService.getCommentsForTask(taskId);
        assertNotNull(commentsBefore);

        // 2. Test add comment
        CommentDto newComment = commentService.addComment(taskId, 14, "Test comment from Spring Boot test");
        assertNotNull(newComment);
        assertNotNull(newComment.getId());
        assertEquals("Test comment from Spring Boot test", newComment.getMessage());

        // 3. Verify comment is returned
        List<CommentDto> commentsAfter = commentService.getCommentsForTask(taskId);
        assertTrue(commentsAfter.stream().anyMatch(c -> c.getId().equals(newComment.getId())));
    }
}
