package com.tms.service;

import com.tms.dto.CommentDto;
import com.tms.entity.Comment;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<CommentDto> getCommentsForTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found");
        }

        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtAscIdAsc(taskId);
        Map<Integer, String> userNames = new HashMap<>();
        for (var u : userRepository.findAll()) {
            if (u.getId() != null) {
                userNames.put(u.getId(), u.getName() != null ? u.getName() : "User #" + u.getId());
            }
        }

        return comments.stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getUserId(),
                        c.getUserId() != null ? userNames.get(c.getUserId()) : null,
                        c.getMessage(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long taskId, Integer userId, String message) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("Task not found");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }

        // Verify if user exists, otherwise keep userId as null or valid
        String userName = null;
        Integer resolvedUserId = null;
        if (userId != null) {
            var userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                resolvedUserId = userId;
                userName = userOpt.get().getName();
            }
        }

        Comment comment = new Comment(taskId, resolvedUserId, message.trim());
        Comment saved = commentRepository.save(comment);

        return new CommentDto(
                saved.getId(),
                saved.getUserId(),
                userName,
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }
}
