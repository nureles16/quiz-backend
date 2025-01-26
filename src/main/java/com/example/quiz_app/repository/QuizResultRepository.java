package com.example.quiz_app.repository;

import com.example.quiz_app.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(Long userId);
    Optional<QuizResult> findByIdAndUserId(Long id, Long userId);
    Optional<QuizResult> findById(Long id);

}
