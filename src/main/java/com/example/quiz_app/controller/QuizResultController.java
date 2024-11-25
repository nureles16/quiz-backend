package com.example.quiz_app.controller;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.service.QuizResultService;
import com.example.quiz_app.service.UserService;
import com.example.quiz_app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/submit")
    public ResponseEntity<QuizResult> submitQuizResult(@RequestBody QuizResult quizResult,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        if (!jwtUtil.isTokenValid(token, username)) {
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (quizResult.getTitle() == null || quizResult.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return userService.getUserByUsername(username).map(user -> {
            quizResult.setUser(user);
            QuizResult savedResult = quizResultService.saveQuizResult(quizResult);
            return ResponseEntity.ok(savedResult);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizResult>> getQuizResultsByUser(@PathVariable Long userId) {
        return userService.getUserById(userId).map(user -> {
            List<QuizResult> results = quizResultService.getResultsByUser(Optional.of(user));
            return ResponseEntity.ok(results);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
