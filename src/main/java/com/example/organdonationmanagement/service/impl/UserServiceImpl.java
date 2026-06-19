package com.example.organdonationmanagement.service.impl;

import com.example.organdonationmanagement.dto.request.ProfileUpdateRequest;
import com.example.organdonationmanagement.dto.request.UserRequest;
import com.example.organdonationmanagement.entity.Hospital;
import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.entity.enums.Role;
import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.repository.UserRepository;
import com.example.organdonationmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User create(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại!");
        }

        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .hospital(hospital)
                .enabled(true)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UserRequest request) {
        User user = getById(id);
        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));
        }

        Optional<User> existedUser = userRepository.findByUsername(request.getUsername());
        if (existedUser.isPresent() && !existedUser.get().getId().equals(id)) {
            throw new RuntimeException("Tên đăng nhập '" + request.getUsername() + "' đã tồn tại!");
        }

        user.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setRole(Role.valueOf(request.getRole()));
        user.setHospital(hospital);

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(getById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void updateProfile(String username, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setAge(request.getAge());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setWorkAddress(request.getWorkAddress());

        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            String fileName = saveFile(request.getAvatarFile());
            user.setAvatarUrl("/uploads/avatars/" + fileName);
        }
        userRepository.save(user);
    }

    @Override
    public void toggleStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + id));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    private String saveFile(MultipartFile file) {
        try {
            String uploadDir = "uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = (originalFilename != null && originalFilename.contains("."))
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".png";

            String newFileName = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file: " + e.getMessage(), e);
        }
    }
}
