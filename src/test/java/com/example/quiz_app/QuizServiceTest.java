package com.example.quiz_app;

import com.example.quiz_app.dto.QuizDTO;
import com.example.quiz_app.entity.Quiz;
import com.example.quiz_app.repository.QuizRepository;
import com.example.quiz_app.service.QuizService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    void getAllQuizzes_ShouldReturnList() {
        List<Quiz> quizzes = List.of(new Quiz(1L, "Math", "Algebra Quiz", "Basic algebra questions"),
                new Quiz(2L, "Science", "Physics Quiz", "Intro to physics"));
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<QuizDTO> result = quizService.getAllQuizzes();

        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getSubject());
        assertEquals("Science", result.get(1).getSubject());
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenQuizExists() {
        Quiz quiz = new Quiz(1L, "Math", "Algebra Quiz", "Basic algebra questions");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        QuizDTO result = quizService.getQuizById(1L);

        assertNotNull(result);
        assertEquals("Math", result.getSubject());
    }

    @Test
    void getQuizById_ShouldThrowException_WhenQuizNotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.getQuizById(1L));
    }

    @Test
    void createQuiz_ShouldSaveAndReturnQuiz() {
        QuizDTO quizDTO = new QuizDTO(null, "Math", "Algebra Quiz", "Basic algebra questions");
        Quiz savedQuiz = new Quiz(1L, "Math", "Algebra Quiz", "Basic algebra questions");

        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        QuizDTO result = quizService.createQuiz(quizDTO);

        assertNotNull(result.getId());
        assertEquals("Math", result.getSubject());
    }

    @Test
    void updateQuiz_ShouldUpdateAndReturnQuiz() {
        Quiz existingQuiz = new Quiz(1L, "Math", "Algebra Quiz", "Basic algebra questions");
        QuizDTO quizDTO = new QuizDTO(1L, "Science", "Physics Quiz", "Intro to physics");
        Quiz updatedQuiz = new Quiz(1L, "Science", "Physics Quiz", "Intro to physics");

        when(quizRepository.findById(1L)).thenReturn(Optional.of(existingQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(updatedQuiz);

        QuizDTO result = quizService.updateQuiz(1L, quizDTO);

        assertEquals("Science", result.getSubject());
    }

    @Test
    void deleteQuiz_ShouldCallRepositoryDelete_WhenQuizExists() {
        Quiz existingQuiz = new Quiz(1L, "Math", "Algebra Quiz", "Basic algebra questions");
        when(quizRepository.findById(1L)).thenReturn(Optional.of(existingQuiz));

        quizService.deleteQuiz(1L);

        verify(quizRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteQuiz_ShouldThrowException_WhenQuizNotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> quizService.deleteQuiz(1L));
    }
}
