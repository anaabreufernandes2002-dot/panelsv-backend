package com.panelsv.backend.controller;

import com.panelsv.backend.repository.SuggestionRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    private final SuggestionRepository suggestionRepository;

    public SuggestionController(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }

    @GetMapping("/contractors")
    public List<String> contractorSuggestions(
            @RequestParam(name = "q", defaultValue = "") String q
    ) {
        String prefix = q.trim();
        if (prefix.isEmpty()) {
            return List.of();
        }
        return suggestionRepository.findContractorsByPrefix(prefix);
    }

    @GetMapping("/materials")
    public List<String> materialSuggestions(
            @RequestParam(name = "q", defaultValue = "") String q
    ) {
        String prefix = q.trim();
        if (prefix.isEmpty()) {
            return List.of();
        }
        return suggestionRepository.findMaterialsByPrefix(prefix);
    }
}
