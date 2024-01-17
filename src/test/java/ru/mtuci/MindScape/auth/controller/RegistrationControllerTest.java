package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.dto.UserRegistrationDto;
import ru.mtuci.MindScape.auth.service.RegistrationService;
import ru.mtuci.MindScape.auth.service.UserService;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void preRegisterUserTest() throws IOException {
        // Arrange
        UserRegistrationDto regDto = new UserRegistrationDto();
        regDto.setEmail("test@example.com");
        regDto.setRole("user");
        MultipartFile documents = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getServletPath()).thenReturn("/registration/user");

        // Act
        ResponseEntity<?> result = registrationController.preRegisterUser(regDto, documents, request, session);

        // Assert
        verify(registrationService).preRegister(session);
        verify(userService).generateCaptcha(session);
        assertEquals(ResponseEntity.ok().body("{}"), result);
    }

    @Test
    void confirmCaptchaTest() {
        // Arrange
        String answer = "42";
        HttpSession session = mock(HttpSession.class);
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@example.com");

        // Установка значений для атрибутов num1 и num2
        when(session.getAttribute("num1")).thenReturn(5);
        when(session.getAttribute("num2")).thenReturn(7);

        when(session.getAttribute("DTO")).thenReturn(userRegistrationDto);

        // Act
        session.setAttribute("num1", 5);
        session.setAttribute("num2", 7);
        ResponseEntity<?> result = registrationController.confirmCaptcha(answer, session);

        // Assert
        verify(userService).checkCaptcha(eq(5), eq(7), eq(Integer.parseInt(answer)), anyString(), anyString());
        assertEquals(ResponseEntity.ok(Map.of("role", "someDefaultValue")), result);
    }

    @Test
    void registerTest() {
        // Arrange
        String code = "123456";
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        UserRegistrationDto regDto = new UserRegistrationDto();
        regDto.setEmail("test@example.com");
        when(session.getAttribute("DTO")).thenReturn(regDto);

        // Act
        String result = registrationController.register(code, redirectAttributes, request, session);

        // Assert
        verify(request).setAttribute(eq("operationType"), eq("registration"));
        verify(userService).validateCode(anyString(), eq(code));
        verify(registrationService).register(session);
        assertEquals("redirect:/login", result);
    }

    @Test
    void resendCodeTest() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        HttpSession session = mock(HttpSession.class);
        UserRegistrationDto regDto = new UserRegistrationDto();
        regDto.setEmail("test@example.com");
        when(session.getAttribute("DTO")).thenReturn(regDto);

        // Act
        String result = registrationController.resendCode(request, redirectAttributes, session);

        // Assert
        verify(userService).createAndSendCode(eq("test@example.com"), anyString());
        assertEquals("redirect:" + request.getHeader("Referer"), result);
    }
}
