package com.example.quiz_app.dto;

import com.example.quiz_app.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginResponse {
    private String token;
    private User user;

}
