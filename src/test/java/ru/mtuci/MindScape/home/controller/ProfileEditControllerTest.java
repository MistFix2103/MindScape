package ru.mtuci.MindScape.home.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.service.UserService;
import ru.mtuci.MindScape.home.service.ProfileEditService;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileEditControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileEditService profileEditService;

    @InjectMocks
    private ProfileEditController profileEditController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteAccount_ValidAuthentication_AccountDeletedAndRedirectedToLogin() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        String result = profileEditController.deleteAccount(authentication, redirectAttributes);

        // Assert
        verify(userRepository, times(1)).deleteByEmail(anyString());
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("Ваш аккаунт удален!"));
        assertEquals("redirect:/login", result);
    }

    @Test
    void changeName_ValidData_NameChangedAndResponseEntityOk() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<?> expectedResponseEntity = ResponseEntity.ok().body(Map.of("highlightContainerName", "name-container"));

        // Act
        doNothing().when(profileEditService).validateName(anyString());
        doNothing().when(profileEditService).changeName(eq("NewName"), anyString());
        ResponseEntity<?> result = profileEditController.changeName("NewName", authentication);

        // Assert
        assertEquals(expectedResponseEntity, result);
        verify(profileEditService, times(1)).changeName(eq("NewName"), anyString());
    }

    @Test
    void preChangeMail_ValidData_NewMailStoredInSessionAndResponseEntityOk() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        ResponseEntity<?> expectedResponseEntity = ResponseEntity.ok().body(Map.of("operationType", "mail_change"));

        // Act
        ResponseEntity<?> result = profileEditController.preChangeMail("new@mail.com", session);

        // Assert
        assertEquals(expectedResponseEntity, result);
        verify(profileEditService, times(1)).preChangeMail(eq("new@mail.com"));
        verify(session, times(1)).setAttribute(eq("newMail"), eq("new@mail.com"));
    }

    @Test
    void changeMail_ValidData_MailChangedAndRedirectedToProfileWithHighlightContainer() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        when(authentication.getName()).thenReturn("test@example.com");
        when(session.getAttribute("newMail")).thenReturn("new@mail.com");

        // Assert
        String result = profileEditController.changeMail("verificationCode", authentication, redirectAttributes, session);
        assertEquals("redirect:/home/profile", result);
        verify(profileEditService, times(1)).changeMail(eq("test@example.com"), eq("new@mail.com"));
        verify(session, times(1)).removeAttribute("newMail");
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("highlightContainerName"), eq("email-container"));
    }

    @Test
    void preChangePass_ValidData_NewPassStoredInSessionAndResponseEntityOk() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        HttpSession session = mock(HttpSession.class);
        ResponseEntity<?> expectedResponseEntity = ResponseEntity.ok().body(Map.of("operationType", "password_change"));

        // Act
        ResponseEntity<?> result = profileEditController.preChangePass("newPassword", "newPassword", authentication, session);

        // Assert
        assertEquals(expectedResponseEntity, result);
        verify(profileEditService, times(1)).preChangePass(eq("newPassword"), eq("newPassword"), anyString());
        verify(session, times(1)).setAttribute(eq("newPass"), eq("newPassword"));
    }

    @Test
    void changePass_ValidData_PassChangedAndRedirectedToProfileWithHighlightContainer() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Act
        when(authentication.getName()).thenReturn("test@example.com");
        when(session.getAttribute("newPass")).thenReturn("newPassword");

        // Assert
        String result = profileEditController.changePass("verificationCode", authentication, redirectAttributes, new MockHttpServletRequest(), session);
        assertEquals("redirect:/home/profile", result);
        verify(profileEditService, times(1)).changePass(eq("test@example.com"), eq("newPassword"));
        verify(session, times(1)).removeAttribute("newPass");
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("highlightContainerName"), eq("pass-container"));
    }


    @Test
    void manage2FA_ValidAuthentication_TwoFAManagedAndResponseEntityOk() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        ResponseEntity<?> expectedResponseEntity = ResponseEntity.ok().body(Map.of("highlightContainerName", "twoFA-container"));

        // Act
        ResponseEntity<?> result = profileEditController.manage2FA(authentication);

        // Assert
        assertEquals(expectedResponseEntity, result);
        verify(profileEditService, times(1)).manage2FA(anyString());
    }

    @Test
    void changeImage_ValidData_ImageChangedAndRedirectedToProfile() throws IOException {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        MultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "content".getBytes());

        // Act
        String result = profileEditController.changeImage(file, authentication);

        // Assert
        verify(profileEditService, times(1)).changeImage(eq(file), anyString());
        assertEquals("redirect:/home/profile", result);
    }

    @Test
    void deleteImage_ValidAuthentication_ImageDeletedAndRedirectedToProfile() {
        // Arrange
        Authentication authentication = mock(Authentication.class);

        // Act
        String result = profileEditController.deleteImage(authentication);

        // Assert
        verify(profileEditService, times(1)).deleteImage(anyString());
        assertEquals("redirect:/home/profile", result);
    }

    @Test
    void resendCode_MailChangeOperation_CodeResentAndRedirectedToVerification() {
        // Arrange
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Authentication authentication = mock(Authentication.class);
        HttpSession session = mock(HttpSession.class);

        // Act
        when(session.getAttribute("newMail")).thenReturn("new@mail.com");
        String result = profileEditController.resendCode("mail_change", redirectAttributes, authentication, session);

        // Assert
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("operationType"), eq("mail_change"));
        verify(userService, times(1)).createAndSendCode(eq("new@mail.com"), eq("mail_change"));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("Код был повторно отправлен."));
        assertEquals("redirect:/home/profile/verification", result);
    }

    @Test
    void resendCode_PasswordChangeOperation_CodeResentAndRedirectedToVerification() {
        // Arrange
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Authentication authentication = mock(Authentication.class);

        // Act
        String result = profileEditController.resendCode("password_change", redirectAttributes, authentication, null);

        // Assert
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("operationType"), eq("password_change"));
        verify(userService, times(1)).createAndSendCode(anyString(), eq("pass_change"));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("Код был повторно отправлен."));
        assertEquals("redirect:/home/profile/verification", result);
    }
}
