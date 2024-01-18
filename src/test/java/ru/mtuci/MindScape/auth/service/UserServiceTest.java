package ru.mtuci.MindScape.auth.service;

import static org.mockito.Mockito.doNothing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.mtuci.MindScape.exceptions.CustomExceptions;
import ru.mtuci.MindScape.user.model.ConfirmationCode;
import ru.mtuci.MindScape.user.repository.ConfirmationCodeRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private EmailService emailService;

    @Mock
    private ConfirmationCodeRepository confirmationCodeRepository;

    @InjectMocks
    private UserService userService;

    UserServiceTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAndSendCode_SuccessfullySent() {
        // Arrange
        String email = "test@example.com";
        String type = "user";
        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setEmail(email);
        confirmationCode.setCode("123456");
        confirmationCode.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        when(confirmationCodeRepository.findByEmail(anyString())).thenReturn(null);
        doNothing().when(emailService).sendCodeEmail(anyString(), anyString(), anyString());


        // Act
        userService.createAndSendCode(email, type);

        // Assert
        verify(confirmationCodeRepository).deleteByEmail(email);
        verify(confirmationCodeRepository).save(any(ConfirmationCode.class));
        verify(emailService).sendCodeEmail(email, "123456", type);
    }

    @Test
    void validateCode_CodeMismatch_ThrowsInvalidCodeException() {
        // Arrange
        String email = "test@example.com";
        String code = "987654";
        ConfirmationCode storedCodeEntity = new ConfirmationCode();
        storedCodeEntity.setCode("123456");

        when(confirmationCodeRepository.findByEmail(anyString())).thenReturn(storedCodeEntity);

        // Act & Assert
        assertThrows(CustomExceptions.InvalidCodeException.class, () -> userService.validateCode(email, code));
    }

    @Test
    void validateCode_CodeExpired_ThrowsCodeExpiredException() {
        // Arrange
        String email = "test@example.com";
        String code = "123456";
        ConfirmationCode storedCodeEntity = new ConfirmationCode();
        storedCodeEntity.setCode(code);
        storedCodeEntity.setExpirationDate(LocalDateTime.now().minusMinutes(1));

        when(confirmationCodeRepository.findByEmail(anyString())).thenReturn(storedCodeEntity);

        // Act & Assert
        assertThrows(CustomExceptions.CodeExpiredException.class, () -> userService.validateCode(email, code));
    }

}
