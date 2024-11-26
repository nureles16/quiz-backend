package com.example.quiz_app.service;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.repository.QuizResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResult saveQuizResult(QuizResult quizResult) {
        if (quizResult.getTitle() == null || quizResult.getSubject() == null) {
            throw new IllegalArgumentException("QuizResult must include a title and subject.");
        }
        return quizResultRepository.save(quizResult);
    }

    public List<QuizResult> getResultsByUser(User user) {
        return quizResultRepository.findByUser(user);
    }

    public void deleteQuizResult(Long id) {
        if (!quizResultRepository.existsById(id)) {
            throw new EntityNotFoundException("QuizResult with ID " + id + " not found.");
        }
        quizResultRepository.deleteById(id);
    }
}
