package com.example.organdonationmanagement.repository;

import com.example.organdonationmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository
        extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
