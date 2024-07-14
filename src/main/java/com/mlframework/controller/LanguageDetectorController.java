package com.mlframework.controller;

import com.mlframework.service.LanguageDetectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/lang-detector")
public class LanguageDetectorController {
    private static final Logger logger = LoggerFactory.getLogger(LanguageDetectorController.class);
    private final LanguageDetectorService service;

    @Autowired
    private LanguageDetectorController(LanguageDetectorService langDetectorService){
        this.service = langDetectorService;
    }

    @PostMapping("/load")
    public String loadModel(@RequestParam String modelBinFile) {
        logger.info("Received request to load Model {}", modelBinFile);
        try {
            service.loadModel(modelBinFile);
            return "Model loaded successfully.";
        } catch (IOException e) {
            return "Error loading Model: " + e.getMessage();
        }
    }

    @GetMapping("/predict")
    public String predictLang(@RequestParam String text) {
        logger.info("Received request to predict language for: {}", text);
        return service.predictLanguage(text);
    }

    @GetMapping("/get-languages")
    public String getLanguages() {
        logger.info("Received request to get model languages");
        return service.getLanguages();
    }

}
