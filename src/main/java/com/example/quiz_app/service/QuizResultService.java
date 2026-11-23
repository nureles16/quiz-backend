package com.example.quiz_app.service;

import com.example.quiz_app.dto.QuizResultDto;
import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.repository.QuizResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResult saveQuizResult(QuizResult dto) {
        if (dto.getTitle() == null || dto.getSubject() == null) {
            throw new IllegalArgumentException("QuizResult must include a title and subject.");
        }
        QuizResult quizResult = new QuizResult();
        quizResult.setUserId(dto.getUserId());
        quizResult.setUsername(dto.getUsername());
        quizResult.setTitle(dto.getTitle());
        quizResult.setSubject(dto.getSubject());
        quizResult.setScore(dto.getScore());
        quizResult.setTotalQuestions(dto.getTotalQuestions());
        quizResult.setUserAnswers(dto.getUserAnswers());
        quizResult.setCorrectAnswers(dto.getCorrectAnswers());

        return quizResultRepository.save(quizResult);
    }

    public List<QuizResultDto> getResultsByUser(Long userId) {
        return quizResultRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<QuizResultDto> getResultsByQuizId(Long id) {
        Optional<QuizResult> results = quizResultRepository.findById(id);
        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }



    @Transactional
    public void deleteQuizResult(Long id) {
        QuizResult quizResult = quizResultRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("QuizResult with ID " + id + " not found."));
        quizResultRepository.delete(quizResult);
    }

    public Optional<QuizResultDto> getQuizResultByIdAndUser(Long id, Long userId) {
        return quizResultRepository.findByIdAndUserId(id, userId).map(this::mapToDto);
    }

    private QuizResultDto mapToDto(QuizResult result) {
        return new QuizResultDto(
                result.getId(),
                result.getUserId(),
                result.getUsername(),
                result.getTitle(),
                result.getSubject(),
                result.getScore(),
                result.getTotalQuestions(),
                result.getUserAnswers(),
                result.getCorrectAnswers(),
                result.getCompletedAt()
        );
    }
}