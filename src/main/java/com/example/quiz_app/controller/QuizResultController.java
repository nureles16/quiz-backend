package com.example.quiz_app.controller;

import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.service.QuizResultService;
import com.example.quiz_app.service.UserService;
import com.example.quiz_app.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private String validateTokenAndGetUsername(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.out.println("Invalid authorization header");
            return null;
        }
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (!jwtUtil.isTokenValid(token, username)) {
            System.out.println("Invalid or expired token");
            SecurityContextHolder.clearContext();
            return null;
        }
        return username;
    }


    @PostMapping("/submit")
    public ResponseEntity<QuizResult> submitQuizResult(@RequestBody QuizResult quizResult,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        String username = validateTokenAndGetUsername(authorizationHeader);
        if (username == null) {
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

    @GetMapping("/user-results")
    public ResponseEntity<List<QuizResult>> getResultsByUser(@RequestHeader("Authorization") String authorizationHeader) {
        String username = validateTokenAndGetUsername(authorizationHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.getUserByUsername(username)
                .map(user -> {
                    List<QuizResult> results = quizResultService.getResultsByUser(user);
                    return results.isEmpty()
                            ? ResponseEntity.noContent().<List<QuizResult>>build()
                            : ResponseEntity.ok(results);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQuizResult(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        String username = validateTokenAndGetUsername(authorizationHeader);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return userService.getUserByUsername(username).map(user -> {
            QuizResult quizResult = quizResultService.getResultsByUser(user).stream()
                    .filter(result -> result.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("QuizResult not found or does not belong to the user."));

            quizResultService.deleteQuizResult(id);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
