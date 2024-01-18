package ru.mtuci.MindScape.auth.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PageControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private PageController pageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showLoginPage_ReturnsCorrectViewName() {
        String result = pageController.showLoginPage();
        assertEquals("login", result);
    }

    @Test
    void showPassRecoverPage_AddsCorrectAttribute() {
        pageController.showPassRecoverPage(model);
        verify(model, times(1)).addAttribute(eq("action"), eq("pass"));
    }

    @Test
    void showRegistrationPage_ReturnsCorrectViewName() {
        String result = pageController.showRegistrationPage();
        assertEquals("registration", result);
    }

    @Test
    void showUserRegistrationPage_ReturnsCorrectViewNameAndAddsAttribute() {
        String userType = "user";
        String result = pageController.showUserRegistrationPage(userType, model);

        verify(model, times(1)).addAttribute(eq("action"), eq("reg"));
        verify(model, never()).addAttribute(eq("userType"), anyString());
        assertEquals("user", result);
    }

    @Test
    void showExpertRegistrationPage_ReturnsCorrectViewNameAndAddsAttribute() {
        String userType = "expert";
        String result = pageController.showUserRegistrationPage(userType, model);

        verify(model, times(1)).addAttribute(eq("action"), eq("reg"));
        verify(model, times(1)).addAttribute(eq("userType"), eq("expert"));
        assertEquals("expert", result);
    }

    @Test
    void showVerificationRegistrationPage_AddsCorrectAttribute() {
        String result = pageController.showVerificationRegistrationPage(model);
        verify(model, times(1)).addAttribute(eq("operationType"), eq("registration"));
        assertEquals("verification", result);
    }

    @Test
    void showVerificationPassRecoverPage_AddsCorrectAttribute() {
        String result = pageController.showVerificationPassRecoverPage(model);
        verify(model, times(1)).addAttribute(eq("operationType"), eq("recovery"));
        assertEquals("verification", result);
    }
}