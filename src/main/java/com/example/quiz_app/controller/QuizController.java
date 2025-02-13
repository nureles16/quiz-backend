package com.example.quiz_app.controller;

import com.example.quiz_app.dto.QuizDTO;
import com.example.quiz_app.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping
    public List<QuizDTO> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public QuizDTO getQuizById(@PathVariable Long id) {
        return quizService.getQuizById(id);
    }

    @PostMapping
    public QuizDTO createQuiz(@RequestBody QuizDTO quizDTO) {
        return quizService.createQuiz(quizDTO);
    }

    @PutMapping("/{id}")
    public QuizDTO updateQuiz(@PathVariable Long id, @RequestBody QuizDTO quizDTO) {
        return quizService.updateQuiz(id, quizDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
    }
}