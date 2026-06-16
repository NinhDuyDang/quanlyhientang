package com.example.organdonationmanagement.dto.request;
import lombok.Data;

@Data
public class UserRequest {

    private String username;
    private String password;
    private String role;
    private Long hospitalId;
}
