package com.example.quiz_app;

import com.example.quiz_app.controller.AuthController;
import com.example.quiz_app.dto.LoginRequest;
import com.example.quiz_app.dto.LoginResponse;
import com.example.quiz_app.dto.RegisterRequest;
import com.example.quiz_app.dto.RegisterResponse;
import com.example.quiz_app.entity.Role;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.service.AuthService;
import com.example.quiz_app.service.UserService;
import com.example.quiz_app.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        RegisterResponse response = RegisterResponse.builder().token("mock-token").build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-token"));
    }

    @Test
    void testRegister_UsernameTaken_ShouldReturnBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");

        when(authService.register(any(RegisterRequest.class))).thenThrow(new IllegalStateException("Username taken"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "password");
        LoginResponse response = LoginResponse.builder()
                .token("jwt-token")
                .user(new User())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void testLogin_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");

        when(authService.login(any(LoginRequest.class))).thenThrow(new IllegalStateException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        Long userId = 1L;
        String token = "Bearer jwt-token";
        String username = "testUser";

        User userDetails = new User();
        userDetails.setUsername(username);
        userDetails.setEmail("user@example.com");

        when(jwtUtil.extractUsername("jwt-token")).thenReturn(username);
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(userDetails);

        mockMvc.perform(put("/auth/user/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void testUpdateUser_Forbidden_WhenUsernameMismatch() throws Exception {
        Long userId = 1L;
        String token = "Bearer jwt-token";

        User userDetails = new User();
        userDetails.setUsername("otherUser");

        when(jwtUtil.extractUsername("jwt-token")).thenReturn("actualUser");

        mockMvc.perform(put("/auth/user/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateUser_NotFound_ShouldReturnNotFound() throws Exception {
        Long userId = 1L;
        String token = "Bearer jwt-token";
        String username = "testUser";

        User userDetails = new User();
        userDetails.setUsername(username);

        when(jwtUtil.extractUsername("jwt-token")).thenReturn(username);
        when(userService.updateUser(eq(userId), any(User.class))).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(put("/auth/user/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDetails)))
                .andExpect(status().isNotFound());
    }
}
