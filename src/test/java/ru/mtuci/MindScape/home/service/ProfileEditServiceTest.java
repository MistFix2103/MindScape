package ru.mtuci.MindScape.home.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ru.mtuci.MindScape.auth.service.UserService;
import ru.mtuci.MindScape.exceptions.CustomExceptions;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProfileEditServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileEditService profileEditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateName_ValidName_NoExceptionThrown() {
        assertDoesNotThrow(() -> profileEditService.validateName("John Doe"));
    }

    @Test
    void validateName_TooLongName_NameIsTooLongExceptionThrown() {
        assertThrows(CustomExceptions.NameIsTooLongException.class, () ->
                profileEditService.validateName("ThisIsAVeryLongNameThatExceedsMaxLength"));
    }

    @Test
    void validateName_InvalidCharacters_IncorrectNameExceptionThrown() {
        assertThrows(CustomExceptions.IncorrectNameException.class, () ->
                profileEditService.validateName("John123"));
    }

    @Test
    void changeName_ValidData_NameChanged() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        profileEditService.changeName("NewName", "test@example.com");

        verify(mockUser, times(1)).setUsername(eq("NewName"));
        verify(userRepository, times(1)).save(eq(mockUser));
    }

    @Test
    void preChangeMail_EmailExists_EmailExistsExceptionThrown() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(User.class)));

        assertThrows(CustomExceptions.EmailExistsException.class, () ->
                profileEditService.preChangeMail("existing@mail.com"));
    }

    @Test
    void changeMail_ValidData_MailChanged() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        Authentication mockAuthentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        profileEditService.changeMail("old@mail.com", "new@mail.com");

        verify(mockUser, times(1)).setEmail(eq("new@mail.com"));
        verify(userRepository, times(1)).save(eq(mockUser));

        verify(mockAuthentication, times(1)).getCredentials();
        verify(mockAuthentication, times(1)).getAuthorities();
        verify(mockAuthentication, times(1)).getPrincipal();
        verify(mockAuthentication, times(1)).getName();
    }

    @Test
    void manage2FA_TwoFAToggled_TwoFAToggled() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        profileEditService.manage2FA("test@example.com");

        verify(mockUser, times(1)).setTwo_step(anyBoolean());
        verify(userRepository, times(1)).save(eq(mockUser));
    }

    @Test
    void changeImage_ValidData_ImageChanged() throws IOException {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        MultipartFile mockImage = mock(MultipartFile.class);
        when(mockImage.getBytes()).thenReturn("image content".getBytes());

        profileEditService.changeImage(mockImage, "test@example.com");

        verify(mockUser, times(1)).setImage(any(byte[].class));
        verify(mockUser, times(1)).setImageBase64(anyString());
        verify(userRepository, times(1)).save(eq(mockUser));
    }

    @Test
    void deleteImage_ImageDeleted() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        profileEditService.deleteImage("test@example.com");

        verify(mockUser, times(1)).setImage(eq(null));
        verify(userRepository, times(1)).save(eq(mockUser));
    }

    @Test
    void preChangePass_ValidData_CodeCreatedAndSent() {
        doNothing().when(userService).validateEmailAndPass(anyString(), anyString(), anyString());
        doNothing().when(userService).createAndSendCode(anyString(), eq("pass_change"));

        profileEditService.preChangePass("newPassword", "newPassword", "test@example.com");

        verify(userService, times(1)).validateEmailAndPass(anyString(), anyString(), anyString());
        verify(userService, times(1)).createAndSendCode(anyString(), eq("pass_change"));
    }


    @Test
    void changePass_ValidData_PasswordChanged() {
        User mockUser = mock(User.class);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        profileEditService.changePass("test@example.com", "newPassword");

        verify(mockUser, times(1)).setPassword(eq("encodedPassword"));
        verify(userRepository, times(1)).save(eq(mockUser));
    }
}

