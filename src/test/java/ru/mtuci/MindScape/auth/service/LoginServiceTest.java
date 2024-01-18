package ru.mtuci.MindScape.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private CsrfToken csrfToken;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authorizeUser_TwoStepEnabled_RedirectTo2fa() throws IOException {
        // Arrange
        User userWithTwoStep = new User();
        userWithTwoStep.setTwo_step(true);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userWithTwoStep));

        // Act
        loginService.authorizeUser(response, authentication, request);

        // Assert
        verify(response).sendRedirect("/login/2fa");
    }

    @Test
    void authorizeUser_TwoStepDisabled_RedirectToHome() throws IOException {
        // Arrange
        User userWithoutTwoStep = new User();
        userWithoutTwoStep.setTwo_step(false);

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userWithoutTwoStep));
        when(request.getAttribute(CsrfToken.class.getName())).thenReturn(csrfToken);

        // Act
        loginService.authorizeUser(response, authentication, request);

        // Assert
        verify(response).addCookie(any());
        verify(response).sendRedirect("/home");
    }
}
