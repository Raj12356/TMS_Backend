package com.tms.controller;

import com.tms.dto.CommentDto;
import com.tms.dto.CommentRequest;
import com.tms.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<?> listComments(@PathVariable Long taskId) {
        try {
            List<CommentDto> comments = commentService.getCommentsForTask(taskId);
            return ResponseEntity.ok(comments);
        } catch (IllegalArgumentException e) {
            log.warn("listComments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to load comments for task " + taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to load comments: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(@PathVariable Long taskId, @RequestBody CommentRequest req) {
        try {
            CommentDto comment = commentService.addComment(taskId, req.getUserId(), req.getMessage());

            // Provide both "comment" wrapper and root fields to support all frontend consumption patterns
            Map<String, Object> response = new HashMap<>();
            response.put("comment", comment);
            response.put("id", comment.getId());
            response.put("userId", comment.getUserId());
            response.put("userName", comment.getUserName());
            response.put("message", comment.getMessage());
            response.put("createdAt", comment.getCreatedAt());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("addComment failed validation: {}", e.getMessage());
            if ("Task not found".equalsIgnoreCase(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to add comment to task " + taskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to add comment: " + e.getMessage()));
        }
    }
}
