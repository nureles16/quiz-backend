package com.example.quiz_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.quiz_app.dto.QuestionDTO;
import com.example.quiz_app.entity.Question;
import com.example.quiz_app.entity.Quiz;
import com.example.quiz_app.repository.QuestionRepository;
import com.example.quiz_app.repository.QuizRepository;
import com.example.quiz_app.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question question;
    private QuestionDTO questionDTO;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Sample Quiz");

        question = new Question();
        question.setId(1L);
        question.setSubject("Math");
        question.setTitle("Sample Title");
        question.setQuestion("What is 2+2?");
        question.setAnswer("4");
        question.setOptions(List.of("1", "2", "3", "4"));
        question.setTopics(List.of("Addition"));
        question.setQuiz(quiz);

        questionDTO = new QuestionDTO(
                1L, "Math", "Sample Title", "What is 2+2?", "4",
                List.of("1", "2", "3", "4"), List.of("Addition"), 1L
        );
    }

    @Test
    void testGetAllQuestions() {
        when(questionRepository.findAll()).thenReturn(List.of(question));

        List<QuestionDTO> result = questionService.getAllQuestions();

        assertEquals(1, result.size());
        assertEquals("Math", result.get(0).getSubject());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void testGetQuestionsByQuizId() {
        when(questionRepository.findByQuizId(1L)).thenReturn(List.of(question));

        List<QuestionDTO> result = questionService.getQuestionsByQuizId(1L);

        assertEquals(1, result.size());
        assertEquals("Sample Title", result.get(0).getTitle());
        verify(questionRepository, times(1)).findByQuizId(1L);
    }

    @Test
    void testGetQuestionById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Optional<QuestionDTO> result = questionService.getQuestionById(1L);

        assertTrue(result.isPresent());
        assertEquals("Math", result.get().getSubject());
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateQuestion() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionDTO savedQuestion = questionService.createQuestion(questionDTO);

        assertNotNull(savedQuestion);
        assertEquals("Math", savedQuestion.getSubject());
        verify(quizRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testCreateQuestion_QuizNotFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> questionService.createQuestion(questionDTO));

        assertEquals("Quiz not found", exception.getMessage());
        verify(quizRepository, times(1)).findById(1L);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testUpdateQuestion() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        Optional<QuestionDTO> updatedQuestion = questionService.updateQuestion(1L, questionDTO);

        assertTrue(updatedQuestion.isPresent());
        assertEquals("Math", updatedQuestion.get().getSubject());
        verify(questionRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testUpdateQuestion_NotFound() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<QuestionDTO> updatedQuestion = questionService.updateQuestion(1L, questionDTO);

        assertFalse(updatedQuestion.isPresent());
        verify(questionRepository, times(1)).findById(1L);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    void testDeleteQuestion() {
        when(questionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(questionRepository).deleteById(1L);

        boolean result = questionService.deleteQuestion(1L);

        assertTrue(result);
        verify(questionRepository, times(1)).existsById(1L);
        verify(questionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteQuestion_NotFound() {
        when(questionRepository.existsById(1L)).thenReturn(false);

        boolean result = questionService.deleteQuestion(1L);

        assertFalse(result);
        verify(questionRepository, times(1)).existsById(1L);
        verify(questionRepository, never()).deleteById(1L);
    }
}
