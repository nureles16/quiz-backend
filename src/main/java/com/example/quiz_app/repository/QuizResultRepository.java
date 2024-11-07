package com.example.quiz_app.repository;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUser(Optional<User> user);

    List<QuizResult> findByQuizId(Long quizId);
}
