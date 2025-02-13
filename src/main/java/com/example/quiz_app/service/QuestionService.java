package com.example.quiz_app.service;

import com.example.quiz_app.dto.QuestionDTO;
import com.example.quiz_app.entity.Question;
import com.example.quiz_app.entity.Quiz;
import com.example.quiz_app.repository.QuestionRepository;
import com.example.quiz_app.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Transactional
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Optional<Quiz> quizOptional = quizRepository.findById(questionDTO.getQuizId());
        if (quizOptional.isEmpty()) {
            throw new RuntimeException("Quiz not found");
        }

        Question question = new Question();
        question.setSubject(questionDTO.getSubject());
        question.setTitle(questionDTO.getTitle());
        question.setQuestion(questionDTO.getQuestion());
        question.setOptions(questionDTO.getOptions());
        question.setAnswer(questionDTO.getAnswer());
        question.setTopics(questionDTO.getTopics());
        question.setQuiz(quizOptional.get());

        Question savedQuestion = questionRepository.save(question);
        return convertToDTO(savedQuestion);
    }

    @Transactional
    public Optional<QuestionDTO> getQuestionById(Long id) {
        return questionRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<QuestionDTO> updateQuestion(Long id, QuestionDTO questionDTO) {
        Optional<Question> existingQuestion = questionRepository.findById(id);
        if (existingQuestion.isPresent()) {
            Question question = existingQuestion.get();
            question.setSubject(questionDTO.getSubject());
            question.setTitle(questionDTO.getTitle());
            question.setQuestion(questionDTO.getQuestion());
            question.setOptions(questionDTO.getOptions());
            question.setAnswer(questionDTO.getAnswer());
            question.setTopics(questionDTO.getTopics());

            questionRepository.save(question);
            return Optional.of(convertToDTO(question));
        }
        return Optional.empty();
    }

    public boolean deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private QuestionDTO convertToDTO(Question question) {
        return new QuestionDTO(
                question.getId(),
                question.getSubject(),
                question.getTitle(),
                question.getQuestion(),
                question.getAnswer(),
                question.getOptions(),
                question.getTopics(),
                question.getQuiz().getId()
        );
    }
}