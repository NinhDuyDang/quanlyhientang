package com.example.organdonationmanagement.service;
import com.example.organdonationmanagement.dto.request.ProfileUpdateRequest; // Import DTO mới
import com.example.organdonationmanagement.dto.request.UserRequest;
import com.example.organdonationmanagement.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> getAll();
    User getById(UUID id);
    Optional<User> findByUsername(String username);
    void updateProfile(String username, ProfileUpdateRequest request);
    void toggleStatus(UUID id);
    User create(UserRequest request);
    User update(UUID id, UserRequest request);
    void delete(UUID id);
}
