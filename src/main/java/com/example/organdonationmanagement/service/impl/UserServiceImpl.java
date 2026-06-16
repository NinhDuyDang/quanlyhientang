package com.example.organdonationmanagement.service.impl;

import com.example.organdonationmanagement.dto.request.UserRequest;
import com.example.organdonationmanagement.entity.Hospital;
import com.example.organdonationmanagement.entity.User;
import com.example.organdonationmanagement.entity.enums.Role;
import com.example.organdonationmanagement.exception.ResourceNotFoundException;
import com.example.organdonationmanagement.repository.HospitalRepository;
import com.example.organdonationmanagement.repository.UserRepository;
import com.example.organdonationmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    @Override
    public User create(UserRequest request) {

        Hospital hospital = null;

        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Hospital not found"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(request.getPassword())
                )
                .role(Role.valueOf(request.getRole()))
                .hospital(hospital)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User update(Long id, UserRequest request) {

        User user = getById(id);

        Hospital hospital = null;

        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Hospital not found"));
        }

        user.setUsername(request.getUsername());

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );
        }

        user.setRole(Role.valueOf(request.getRole()));
        user.setHospital(hospital);

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }
}
