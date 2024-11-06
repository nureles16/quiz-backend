package com.example.quiz_app.service;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResult saveQuizResult(QuizResult quizResult) {
        return quizResultRepository.save(quizResult);
    }

    public List<QuizResult> getResultsByUser(Optional<User> user) {
        return quizResultRepository.findByUser(user);
    }

    public List<QuizResult> getResultsByQuizId(Long quizId) {
        return quizResultRepository.findByQuizId(quizId);
    }
}
