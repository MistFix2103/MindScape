package ru.mtuci.MindScape.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.mtuci.MindScape.auth.dto.PassRecoverDto;
import ru.mtuci.MindScape.auth.service.PassRecoverService;
import ru.mtuci.MindScape.auth.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PassRecoverControllerTest {

    @Mock
    private PassRecoverService passRecoverService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PassRecoverController passRecoverController;

    private MockMvc mockMvc;

    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(passRecoverController).build();
        session = mockMvc.perform(post("/forgot_password")).andReturn().getRequest().getSession();
    }

    @Test
    void preRecover() throws Exception {
        PassRecoverDto passRecoverDto = new PassRecoverDto();
        passRecoverDto.setEmail("test@example.com");

        mockMvc.perform(post("/forgot_password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .flashAttr("PassDTO", passRecoverDto))
                .andExpect(status().isOk());

        assertThat(session.getAttribute("PassDTO")).isEqualTo(passRecoverDto);
        verify(passRecoverService, times(1)).preChange(session);
        verify(userService, times(1)).generateCaptcha(session);
    }

    @Test
    void confirmCaptcha() throws Exception {
        // Set up the session with PassRecoverDto
        PassRecoverDto passRecoverDto = new PassRecoverDto();
        passRecoverDto.setEmail("test@example.com");
        session.setAttribute("PassDTO", passRecoverDto);

        // Set up the expected values for num1 and num2
        int expectedNum1 = 5;
        int expectedNum2 = 7;
        session.setAttribute("num1", expectedNum1);
        session.setAttribute("num2", expectedNum2);

        // Perform the request
        mockMvc.perform(post("/forgot_password/confirmCaptcha")
                        .param("captcha", "42")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk());

        // Verify that checkCaptcha is called with the expected values
        PassRecoverDto passDTO = (PassRecoverDto) session.getAttribute("PassDTO");
        verify(userService, times(1)).checkCaptcha(eq(expectedNum1), eq(expectedNum2), eq(42), eq("recover"), eq(passDTO.getEmail()));
    }

    @Test
    void recover() throws Exception {
        mockMvc.perform(post("/forgot_password/verification")
                        .param("code", "123456")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection());

        PassRecoverDto passDto = (PassRecoverDto) session.getAttribute("PassDTO");
        verify(request, times(1)).setAttribute(eq("operationType"), eq("recovery"));
        verify(userService, times(1)).validateCode(eq(passDto.getEmail()), eq("123456"));
        verify(passRecoverService, times(1)).recover(session);
    }

    @Test
    void resendCode() throws Exception {
        mockMvc.perform(post("/forgot_password/resendCode")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().is3xxRedirection());

        String email = ((PassRecoverDto) session.getAttribute("PassDTO")).getEmail();
        verify(userService, times(1)).createAndSendCode(eq(email), eq("recover"));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("operationType"), eq("recovery"));
        verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), eq("Code has been resent."));
    }
}
