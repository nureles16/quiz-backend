package com.example.quiz_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.quiz_app.dto.LoginRequest;
import com.example.quiz_app.dto.LoginResponse;
import com.example.quiz_app.dto.RegisterRequest;
import com.example.quiz_app.dto.RegisterResponse;
import com.example.quiz_app.entity.Role;
import com.example.quiz_app.entity.User;
import com.example.quiz_app.repository.UserRepository;
import com.example.quiz_app.service.AuthService;
import com.example.quiz_app.service.UserService;
import com.example.quiz_app.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void testLogin_Success() {
        // Given
        String username = "testUser";
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        String jwtToken = "mocked-jwt-token";

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(encodedPassword);

        LoginRequest loginRequest = new LoginRequest(username, rawPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(username)).thenReturn(jwtToken);

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertEquals(mockUser, response.getUser());

        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtUtil, times(1)).generateToken(username);
    }

    @Test
    void testLogin_InvalidPassword_ShouldThrowException() {
        String username = "testUser";
        String wrongPassword = "wrongPassword";
        String correctPassword = passwordEncoder.encode("password123");

        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword(correctPassword);

        LoginRequest loginRequest = new LoginRequest(username, wrongPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

        verify(userRepository, times(1)).findByUsername(username);
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRegister_Success() {
        String username = "newUser";
        String email = "newUser@example.com";
        String password = "securePassword";
        String encodedPassword = passwordEncoder.encode(password);
        String jwtToken = "mocked-jwt-token";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setRole(Role.valueOf("USER"));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setRole(Role.USER);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(jwtUtil.generateToken(username)).thenReturn(jwtToken);

        RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(userService, times(1)).createUser(any(User.class));
        verify(jwtUtil, times(1)).generateToken(username);
    }

    @Test
    void testRegister_UsernameAlreadyExists_ShouldThrowException() {
        // Given
        String username = "existingUser";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> authService.register(registerRequest));

        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userService, never()).createUser(any(User.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRegister_EmailAlreadyExists_ShouldThrowException() {
        String email = "existing@example.com";
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> authService.register(registerRequest));

        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, never()).findByUsername(anyString());
        verify(userService, never()).createUser(any(User.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
