package com.example.quiz_app.service;

import com.example.quiz_app.dto.QuizDTO;
import com.example.quiz_app.entity.Quiz;
import com.example.quiz_app.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizDTO getQuizById(Long id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        return quiz.map(this::convertToDTO).orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Quiz quiz = new Quiz();
        quiz.setSubject(quizDTO.getSubject());
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());

        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    public QuizDTO updateQuiz(Long id, QuizDTO quizDTO) {
        Optional<Quiz> existingQuiz = quizRepository.findById(id);
        if (existingQuiz.isEmpty()) {
            throw new RuntimeException("Quiz not found");
        }

        Quiz quiz = existingQuiz.get();
        quiz.setSubject(quizDTO.getSubject());
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());

        Quiz updatedQuiz = quizRepository.save(quiz);
        return convertToDTO(updatedQuiz);
    }

    public void deleteQuiz(Long id) {
        Optional<Quiz> existingQuiz = quizRepository.findById(id);
        if (existingQuiz.isEmpty()) {
            throw new RuntimeException("Quiz not found");
        }
        quizRepository.deleteById(id);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getSubject(),
                quiz.getTitle(),
                quiz.getDescription()
        );
    }
}