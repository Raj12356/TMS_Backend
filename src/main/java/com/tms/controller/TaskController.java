package com.tms.controller;

import com.tms.dto.TaskRequest;
import com.tms.dto.TaskResponse;
import com.tms.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer assignedTo,
            @RequestParam(required = false, defaultValue = "false") Boolean includeTransfers) {
        return ResponseEntity.ok(taskService.getTasks(userId, assignedTo, Boolean.TRUE.equals(includeTransfers)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        Optional<TaskResponse> taskOpt = taskService.getTaskById(id);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
        }
        return ResponseEntity.ok(taskOpt.get());
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest req) {
        try {
            TaskResponse created = taskService.createTask(req);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "POST failed"));
        }
    }

    @PatchMapping
    public ResponseEntity<?> patchTask(@RequestBody TaskRequest req) {
        if (req.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Task id is required"));
        }
        try {
            Optional<TaskResponse> updated = taskService.updateTask(req.getId(), req, true);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
            }
            return ResponseEntity.ok(updated.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "PATCH failed"));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchTaskPath(@PathVariable Long id, @RequestBody TaskRequest req) {
        try {
            Optional<TaskResponse> updated = taskService.updateTask(id, req, true);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
            }
            return ResponseEntity.ok(updated.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "PATCH failed"));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTaskBody(@RequestBody TaskRequest req) {
        if (req.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Task id is required"));
        }
        try {
            Optional<TaskResponse> updated = taskService.updateTask(req.getId(), req, false);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
            }
            return ResponseEntity.ok(updated.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "UPDATE failed"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaskPath(@PathVariable Long id, @RequestBody TaskRequest req) {
        try {
            Optional<TaskResponse> updated = taskService.updateTask(id, req, false);
            if (updated.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Task not found"));
            }
            return ResponseEntity.ok(updated.get());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "UPDATE failed"));
        }
    }

    // Handles DELETE /api/tasks?id=123
    @DeleteMapping
    public ResponseEntity<?> deleteTaskQuery(@RequestParam(required = false) Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid id"));
        }
        taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // Handles DELETE /api/tasks/123
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskPath(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}

