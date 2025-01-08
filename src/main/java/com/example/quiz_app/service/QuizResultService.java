package com.example.quiz_app.service;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.repository.QuizResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
//    public Page<QuizResult> getResultsByUser(User user, Pageable pageable) {
//        return quizResultRepository.findByUser(user, pageable);
//    }

    @Transactional
    public void deleteQuizResult(Long id) {
        if (!quizResultRepository.existsById(id)) {
            throw new EntityNotFoundException("QuizResult with ID " + id + " not found.");
        }
        quizResultRepository.deleteById(id);
    }

    public Optional<QuizResult> getQuizResultByIdAndUser(Long id, User user) {
        return quizResultRepository.findByIdAndUser(id, user);
    }
}
