package com.example.quiz_app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String subject;
    private String title;
    private String question;
    private String answer;
    private List<String> options;
    private List<String> topics;
    private Long quizId;
}