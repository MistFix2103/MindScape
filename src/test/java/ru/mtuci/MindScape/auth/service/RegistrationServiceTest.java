package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationService registrationService;

    RegistrationServiceTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void preRegister_ValidUser_NoExceptionsThrown() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        UserRegistrationDto regDto = new UserRegistrationDto();
        regDto.setEmail("test@example.com");
        regDto.setName("John Doe");
        regDto.setPassword("password");
        regDto.setConfirmPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        session.setAttribute("DTO", regDto);
        assertDoesNotThrow(() -> registrationService.preRegister(session));
    }

    @Test
    void register_ValidUser_NoExceptionsThrown() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        UserRegistrationDto regDto = new UserRegistrationDto();
        regDto.setName("John Doe");
        regDto.setEmail("test@example.com");
        regDto.setPassword("password");
        regDto.setConfirmPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        session.setAttribute("DTO", regDto);
        assertDoesNotThrow(() -> registrationService.register(session));
        verify(userRepository, times(1)).save(any(User.class));
    }

}
