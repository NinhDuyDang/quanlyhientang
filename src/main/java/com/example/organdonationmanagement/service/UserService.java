package com.example.organdonationmanagement.service;

import com.example.organdonationmanagement.dto.request.UserRequest;
import com.example.organdonationmanagement.entity.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getById(Long id);

    User create(UserRequest request);

    User update(Long id, UserRequest request);

    void delete(Long id);
}
