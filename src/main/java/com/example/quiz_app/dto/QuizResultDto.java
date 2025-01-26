package com.example.quiz_app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {

    @Schema(description = "Quiz result ID")
    private Long id;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Username of the user")
    private String username;

    @Schema(description = "Title of the quiz")
    private String title;

    @Schema(description = "Subject of the quiz")
    private String subject;

    @Schema(description = "Score obtained by the user")
    private int score;

    @Schema(description = "Total number of questions in the quiz")
    private int totalQuestions;

    @Schema(description = "User's answers for the quiz questions",
            example = "{ \"1\": \"Answer 1\", \"2\": \"Answer 2\" }")
    @JsonDeserialize(keyAs = String.class)
    private Map<String, String> userAnswers = new HashMap<>();

    @Schema(description = "Correct answers for the quiz questions",
            example = "{ \"1\": \"Correct Answer 1\", \"2\": \"Correct Answer 2\" }")
    @JsonDeserialize(keyAs = String.class)
    private Map<String, String> correctAnswers = new HashMap<>();

    @Schema(description = "Timestamp when the quiz was completed")
    private LocalDateTime completedAt;
}
