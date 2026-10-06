package com.tms.service;

import com.tms.dto.LoginRequest;
import com.tms.dto.RegisterRequest;
import com.tms.dto.UserDto;
import com.tms.entity.User;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import com.tms.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    public static final List<String> VALID_ROLES = Arrays.asList("admin", "manager", "team member", "user");

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    public UserService(UserRepository userRepository, TaskRepository taskRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Transactional
    public UserDto register(RegisterRequest req) {
        if (req.getName() == null || req.getName().isBlank() ||
            req.getEmail() == null || req.getEmail().isBlank() ||
            req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }

        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("User already exists");
        }

        String hashedPassword = PasswordUtil.hashPassword(req.getPassword());
        User user = new User(req.getName().trim(), email, hashedPassword, "user");
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public UserDto login(LoginRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank() ||
            req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(req.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!PasswordUtil.verifyPassword(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Upgrade legacy plain-text password to scrypt hash if needed
        if (!PasswordUtil.isHashed(user.getPassword())) {
            user.setPassword(PasswordUtil.hashPassword(req.getPassword()));
            userRepository.save(user);
        }

        return toDto(user);
    }

    @Transactional
    public UserDto updateUserRole(Integer id, String role) {
        if (role == null || !VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setRole(role);
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public boolean deleteUser(Integer id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return false;
        }

        // 1. Delete tasks created by this user and their comments
        var tasksCreated = taskRepository.findByUserIdOrderByIdAsc(id);
        for (var task : tasksCreated) {
            commentRepository.deleteByTaskId(task.getId());
            taskRepository.delete(task);
        }

        // 2. Unassign tasks assigned to this user
        var tasksAssigned = taskRepository.findByAssignedToOrderByIdAsc(id);
        for (var task : tasksAssigned) {
            task.setAssignedTo(null);
            taskRepository.save(task);
        }

        // 3. Delete user
        userRepository.deleteById(id);
        return true;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}

