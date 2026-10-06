package com.tms.service;

import com.tms.dto.CommentDto;
import com.tms.dto.TaskRequest;
import com.tms.dto.TaskResponse;
import com.tms.entity.Comment;
import com.tms.entity.Task;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> getTasks(Integer userId, Integer assignedTo) {
        List<Task> tasks;
        if (userId != null && assignedTo != null) {
            tasks = taskRepository.findByUserIdAndAssignedToOrderByIdAsc(userId, assignedTo);
        } else if (userId != null) {
            tasks = taskRepository.findByUserIdOrderByIdAsc(userId);
        } else if (assignedTo != null) {
            tasks = taskRepository.findByAssignedToOrderByIdAsc(assignedTo);
        } else {
            tasks = taskRepository.findAllByOrderByIdAsc();
        }

        return attachComments(tasks);
    }

    public Optional<TaskResponse> getTaskById(Long id) {
        return taskRepository.findById(id).map(task -> {
            List<TaskResponse> responses = attachComments(Collections.singletonList(task));
            return responses.isEmpty() ? null : responses.get(0);
        });
    }

    @Transactional
    public TaskResponse createTask(TaskRequest req) {
        if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (req.getUserId() != null && !userRepository.existsById(req.getUserId())) {
            throw new IllegalStateException("Referenced user does not exist");
        }
        if (req.getAssignedTo() != null && !userRepository.existsById(req.getAssignedTo())) {
            throw new IllegalStateException("Referenced user does not exist");
        }

        Task task = new Task();
        task.setId(req.getId() != null ? req.getId() : System.currentTimeMillis());
        task.setUserId(req.getUserId());
        task.setAssignedTo(req.getAssignedTo());
        task.setTitle(req.getTitle().trim());
        task.setDescription(req.getDescription());
        task.setDueDate(req.getDueDate());
        task.setPriority(req.getPriority());
        task.setCategory(req.getCategory());
        task.setAttachments(req.getAttachments() != null ? req.getAttachments() : new ArrayList<>());

        boolean completed = Boolean.TRUE.equals(req.getCompleted());
        task.setCompleted(completed);

        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            task.setStatus(req.getStatus());
        } else {
            task.setStatus(completed ? "completed" : "pending");
        }

        Task saved = taskRepository.save(task);
        return toResponse(saved, Collections.emptyList());
    }

    @Transactional
    public Optional<TaskResponse> updateTask(Long id, TaskRequest req, boolean sync) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return Optional.empty();
        }

        Task task = taskOpt.get();

        if (req.getUserId() != null) {
            if (!userRepository.existsById(req.getUserId())) {
                throw new IllegalStateException("Referenced user does not exist");
            }
            task.setUserId(req.getUserId());
        }
        if (req.getAssignedTo() != null) {
            if (!userRepository.existsById(req.getAssignedTo())) {
                throw new IllegalStateException("Referenced user does not exist");
            }
            task.setAssignedTo(req.getAssignedTo());
        }

        if (req.getTitle() != null) {
            task.setTitle(req.getTitle().trim());
        }
        if (req.getDescription() != null) {
            task.setDescription(req.getDescription());
        }
        if (req.getDueDate() != null) {
            task.setDueDate(req.getDueDate());
        }
        if (req.getPriority() != null) {
            task.setPriority(req.getPriority());
        }
        if (req.getCategory() != null) {
            task.setCategory(req.getCategory());
        }
        if (req.getAttachments() != null) {
            task.setAttachments(req.getAttachments());
        }

        if (req.getCompleted() != null) {
            task.setCompleted(req.getCompleted());
            if (sync) {
                task.setStatus(req.getCompleted() ? "completed" : "pending");
            }
        }

        if (req.getStatus() != null) {
            task.setStatus(req.getStatus());
            if (sync) {
                task.setCompleted("completed".equalsIgnoreCase(req.getStatus()));
            }
        }

        Task saved = taskRepository.save(task);
        List<TaskResponse> responses = attachComments(Collections.singletonList(saved));
        return Optional.of(responses.get(0));
    }

    @Transactional
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }
        commentRepository.deleteByTaskId(id);
        taskRepository.deleteById(id);
        return true;
    }

    private List<TaskResponse> attachComments(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return Collections.emptyList();
        }

        // Cache user names for comment author display
        Map<Integer, String> userNames = new HashMap<>();
        for (var u : userRepository.findAll()) {
            if (u.getId() != null) {
                userNames.put(u.getId(), u.getName() != null ? u.getName() : "User #" + u.getId());
            }
        }

        List<TaskResponse> result = new ArrayList<>();
        for (Task t : tasks) {
            List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtAscIdAsc(t.getId());
            List<CommentDto> commentDtos = comments.stream()
                    .map(c -> new CommentDto(
                            c.getId(),
                            c.getUserId(),
                            c.getUserId() != null ? userNames.get(c.getUserId()) : null,
                            c.getMessage(),
                            c.getCreatedAt()
                    ))
                    .collect(Collectors.toList());

            result.add(toResponse(t, commentDtos));
        }
        return result;
    }

    private TaskResponse toResponse(Task t, List<CommentDto> comments) {
        TaskResponse res = new TaskResponse();
        res.setId(t.getId());
        res.setUserId(t.getUserId());
        res.setAssignedTo(t.getAssignedTo());
        res.setTitle(t.getTitle());
        res.setDescription(t.getDescription());
        res.setDueDate(t.getDueDate());
        res.setPriority(t.getPriority());
        res.setCategory(t.getCategory());
        res.setStatus(t.getStatus());
        res.setCompleted(t.getCompleted());
        res.setAttachments(t.getAttachments());
        res.setComments(comments != null ? comments : new ArrayList<>());
        return res;
    }
}

