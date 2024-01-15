/**
 * <p>Описание:</p>
 * Контроллер, обрабатывающий страницы приложения, связанные с использованием сервиса.
 */

package ru.mtuci.MindScape.home.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mtuci.MindScape.story.model.Story;
import ru.mtuci.MindScape.story.model.StoryStatus;
import ru.mtuci.MindScape.story.repository.StoryRepository;
import ru.mtuci.MindScape.user.model.User;
import ru.mtuci.MindScape.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/home")
@AllArgsConstructor
public class HomePageController {
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @GetMapping
    public String home(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        model.addAttribute("userImage", user.getImageBase64());
        prepareStories(model, StoryStatus.PUBLISHED, user.getUsername(), user.getId());
        return "home";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("showStatus", !String.valueOf(user.getRole()).equals("USER"));
        model.addAttribute("userImage", user.getImageBase64());
        return "profile";
    }

    @GetMapping("/profile/verification")
    public String showVerificationPage(HttpSession session, Model model){
        Optional<String> newMail = Optional.ofNullable((String) session.getAttribute("newMail"));
        Optional<String> newPass = Optional.ofNullable((String) session.getAttribute("newPass"));
        newMail.ifPresent(mail -> model.addAttribute("operationType", "mail_change"));
        newPass.ifPresent(pass -> model.addAttribute("operationType", "password_change"));
        return "verification";
    }

    @GetMapping("/drafts")
    public String showDraftPage(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        model.addAttribute("userImage", user.getImageBase64());
        prepareStories(model, StoryStatus.DRAFT, user.getUsername(), user.getId());
        return "drafts";
    }

    @GetMapping("/my-stories")
    public String showMyStoriesPage(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).get();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("userImage", user.getImageBase64());
        List<Story> stories = storyRepository.findByAuthor_IdAndStatus(user.getId(), StoryStatus.PUBLISHED);
        model.addAttribute("stories", stories);
        return "my-stories";
    }

    private void prepareStories(Model model, StoryStatus status, String username, UUID id) {
        List<Story> stories = (status.equals(StoryStatus.PUBLISHED))
                                            ? storyRepository.findAllByStatusOrderByTimeDesc(status)
                                            : storyRepository.findByAuthor_IdAndStatus(id, StoryStatus.DRAFT);
        if (status.equals(StoryStatus.PUBLISHED)) {
            stories.forEach(story -> {
                if (story.getText().length() > 300) {
                    story.setText(story.getText().substring(0, 300) + "...");
                }
            });
        }
        model.addAttribute("stories", stories);
        model.addAttribute("username", username);
    }
}