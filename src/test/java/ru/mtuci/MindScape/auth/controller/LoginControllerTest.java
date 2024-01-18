package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import ru.mtuci.MindScape.auth.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    @Test
    void showVerificationPageTest() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        Model model = mock(Model.class);

        // Expected value
        when(authentication.getName()).thenReturn("username");

        // Act
        String result = loginController.showVerificationPage(authentication, model);

        // Assert
        verify(userService).createAndSendCode(eq("username"), eq("two-step"));
        verify(model).addAttribute(eq("operationType"), eq("2fa"));
        assertEquals("verification", result);
    }

    @Test
    void verifyTest() {
        // Arrange
        String code = "123456";
        Authentication authentication = mock(Authentication.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(authentication.getName()).thenReturn("username");

        // Act
        String result = loginController.verify(code, authentication, request);

        // Assert
        verify(request).setAttribute(eq("operationType"), eq("2fa"));
        verify(userService).validateCode(eq("username"), eq(code));
        assertEquals("redirect:/home", result);
    }
}
