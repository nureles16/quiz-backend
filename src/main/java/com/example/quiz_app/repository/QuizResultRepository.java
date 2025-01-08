package com.example.quiz_app.repository;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUser(User user);
    List<QuizResult> findBySubject(String subject);
    Optional<QuizResult> findByIdAndUser(Long id, User user);
}
