package com.example.quiz_app.controller;

import com.example.quiz_app.dto.QuizResultDto;
import com.example.quiz_app.entity.QuizResult;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.service.QuizResultService;
import com.example.quiz_app.service.UserService;
import com.example.quiz_app.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/quiz-results")
@RequiredArgsConstructor
public class QuizResultController {

    private final QuizResultService quizResultService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    private Optional<User> getUserFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        if (!jwtUtil.isTokenValid(token, username)) {
            SecurityContextHolder.clearContext();
            return Optional.empty();
        }
        return userService.getUserByUsername(username);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResultDto> submitQuizResult(@Valid @RequestBody QuizResultDto quizResultDto,
                                                       @RequestHeader("Authorization") String authorizationHeader) {
        return getUserFromToken(authorizationHeader)
                .map(user -> {
                    QuizResult quizResult = new QuizResult(
                            null, user.getId(), user.getUsername(), quizResultDto.getTitle(), quizResultDto.getSubject(),
                            quizResultDto.getScore(), quizResultDto.getTotalQuestions(), quizResultDto.getUserAnswers(),
                            quizResultDto.getCorrectAnswers(), LocalDateTime.now()
                    );

                    QuizResult savedResult = quizResultService.saveQuizResult(quizResult);
                    return ResponseEntity.ok(mapToDto(savedResult));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/user-results")
    public ResponseEntity<List<QuizResultDto>> getResultsByUser(@RequestHeader("Authorization") String authorizationHeader) {
        return getUserFromToken(authorizationHeader)
                .map(user -> {
                    List<QuizResultDto> results = quizResultService.getResultsByUser(user.getId());
                    return results.isEmpty()
                            ? ResponseEntity.noContent().<List<QuizResultDto>>build()
                            : ResponseEntity.ok(results);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteQuizResult(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String authorizationHeader) {
        return getUserFromToken(authorizationHeader)
                .map(user -> {
                    QuizResultDto quizResult = quizResultService.getQuizResultByIdAndUser(id, user.getId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "QuizResult not found"));

                    quizResultService.deleteQuizResult(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }


    @GetMapping("/{id}/feedback")
    public ResponseEntity<Map<String, Map<String, String>>> getQuizFeedback(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        return getUserFromToken(authorizationHeader)
                .flatMap(user -> quizResultService.getQuizResultByIdAndUser(id, user.getId()))
                .map(quizResult -> {
                    Map<String, Map<String, String>> feedback = Map.of(
                            "userAnswers", quizResult.getUserAnswers(),
                            "correctAnswers", quizResult.getCorrectAnswers()
                    );
                    return ResponseEntity.ok(feedback);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/user-results/{id}")
    public ResponseEntity<List<QuizResultDto>> getResultsByQuizId(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {

        return getUserFromToken(authorizationHeader)
                .map(user -> {
                    List<QuizResultDto> results = quizResultService.getResultsByQuizId(id);
                    return results.isEmpty()
                            ? ResponseEntity.noContent().<List<QuizResultDto>>build()
                            : ResponseEntity.ok(results);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
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
