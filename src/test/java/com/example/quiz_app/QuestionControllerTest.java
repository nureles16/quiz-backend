package com.example.quiz_app;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.quiz_app.controller.QuestionController;
import com.example.quiz_app.dto.QuestionDTO;
import com.example.quiz_app.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuestionController questionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private QuestionDTO questionDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionController).build();

        questionDTO = new QuestionDTO(
                1L, "Math", "Sample Title", "What is 2+2?", "4",
                List.of("1", "2", "3", "4"), List.of("Addition"), 1L
        );
    }

    @Test
    void testGetAllQuestions() throws Exception {
        when(questionService.getAllQuestions()).thenReturn(List.of(questionDTO));

        mockMvc.perform(get("/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].subject").value("Math"));

        verify(questionService, times(1)).getAllQuestions();
    }

    @Test
    void testGetQuestionsByQuizId() throws Exception {
        when(questionService.getQuestionsByQuizId(1L)).thenReturn(List.of(questionDTO));

        mockMvc.perform(get("/questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Sample Title"));

        verify(questionService, times(1)).getQuestionsByQuizId(1L);
    }

    @Test
    void testGetQuestionById_Found() throws Exception {
        when(questionService.getQuestionById(1L)).thenReturn(Optional.of(questionDTO));

        mockMvc.perform(get("/questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("What is 2+2?"));

        verify(questionService, times(1)).getQuestionById(1L);
    }

    @Test
    void testGetQuestionById_NotFound() throws Exception {
        when(questionService.getQuestionById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/questions/1"))
                .andExpect(status().isNotFound());

        verify(questionService, times(1)).getQuestionById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateQuestion() throws Exception {
        when(questionService.createQuestion(any(QuestionDTO.class))).thenReturn(questionDTO);

        mockMvc.perform(post("/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("Math"));

        verify(questionService, times(1)).createQuestion(any(QuestionDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateQuestion_Found() throws Exception {
        when(questionService.updateQuestion(eq(1L), any(QuestionDTO.class)))
                .thenReturn(Optional.of(questionDTO));

        mockMvc.perform(put("/questions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Title"));

        verify(questionService, times(1)).updateQuestion(eq(1L), any(QuestionDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateQuestion_NotFound() throws Exception {
        when(questionService.updateQuestion(eq(1L), any(QuestionDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/questions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(questionDTO)))
                .andExpect(status().isNotFound());

        verify(questionService, times(1)).updateQuestion(eq(1L), any(QuestionDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteQuestion_Success() throws Exception {
        when(questionService.deleteQuestion(1L)).thenReturn(true);

        mockMvc.perform(delete("/questions/1"))
                .andExpect(status().isNoContent());

        verify(questionService, times(1)).deleteQuestion(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteQuestion_NotFound() throws Exception {
        when(questionService.deleteQuestion(1L)).thenReturn(false);

        mockMvc.perform(delete("/questions/1"))
                .andExpect(status().isNotFound());

        verify(questionService, times(1)).deleteQuestion(1L);
    }

    @Test
    void testGetQuestionsByTopic() throws Exception {
        when(questionService.getAllQuestions()).thenReturn(List.of(questionDTO));

        mockMvc.perform(get("/questions/topic/Addition"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].topics[0]").value("Addition"));

        verify(questionService, times(1)).getAllQuestions();
    }
}
