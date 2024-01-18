package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mtuci.MindScape.auth.dto.PassRecoverDto;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class PassRecoverServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PassRecoverService passRecoverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void preChange_ValidData_NoExceptionsThrown() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        PassRecoverDto passRecoverDto = new PassRecoverDto();
        passRecoverDto.setEmail("test@example.com");
        passRecoverDto.setPassword("password");
        passRecoverDto.setConfirmPassword("password");

        // Mocking userService behavior
        doNothing().when(userService).validateEmailAndPass(anyString(), anyString(), anyString());

        // Act & Assert
        assertDoesNotThrow(() -> passRecoverService.preChange(session));
        verify(userService, times(1)).validateEmailAndPass(eq("test@example.com"), eq("password"), eq("password"));
    }

    @Test
    void recover_ValidData_NoExceptionsThrown() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        PassRecoverDto passRecoverDto = new PassRecoverDto();
        passRecoverDto.setEmail("test@example.com");
        passRecoverDto.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");

        // Mocking userRepository and passwordEncoder behavior
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        // Act & Assert
        assertDoesNotThrow(() -> passRecoverService.recover(session));
        verify(userRepository, times(1)).save(eq(user));
        verify(session, times(1)).removeAttribute(eq("PassDTO"));
    }
}