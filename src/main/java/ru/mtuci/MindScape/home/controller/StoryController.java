package ru.mtuci.MindScape.home.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mtuci.MindScape.home.service.StoryService;
import ru.mtuci.MindScape.story.repository.StoryRepository;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final StoryRepository storyRepository;

    @PostMapping("/home/addStory")
    public String addStory(@RequestParam("story") String text, Authentication authentication) {
        storyService.saveStory(text, authentication.getName(), true);
        return "redirect:/home";
    }

    @PostMapping("/home/deleteStory")
    public String deleteStory(@RequestParam("storyId") String id, HttpServletRequest request) {
        storyRepository.deleteById(UUID.fromString(id));
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/home/editStory")
    public String editStory(@RequestParam("story") String newText, @RequestParam("storyId") String id) {
        storyService.editStory(newText, UUID.fromString(id));
        return "redirect:/home/my-stories";
    }

    @PostMapping("/home/saveDraft")
    @ResponseBody
    public ResponseEntity<?> saveDraft(@RequestParam("story") String text, Authentication authentication) {
        storyService.saveStory(text, authentication.getName(), false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/home/saveEditedDraft")
    @ResponseBody
    public ResponseEntity<?> saveEditedDraft(@RequestParam("story") String newText, @RequestParam("storyId") String id) {
        storyService.editStory(newText, UUID.fromString(id));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/home/publishDraft")
    public String publishDraft(@RequestParam("story") String storyText, @RequestParam("storyId") String id) {
        storyService.publishDraft(storyText, UUID.fromString(id));
        return "redirect:/home";
    }
}