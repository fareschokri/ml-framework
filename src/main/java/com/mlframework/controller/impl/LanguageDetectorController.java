package com.mlframework.controller.impl;

import com.mlframework.controller.itf.ModelController;
import com.mlframework.service.impl.LanguageDetectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/lang-detector")
public class LanguageDetectorController implements ModelController {
    private static final Logger logger = LoggerFactory.getLogger(LanguageDetectorController.class);
    private final LanguageDetectorService service;

    @Autowired
    private LanguageDetectorController(LanguageDetectorService langDetectorService){
        this.service = langDetectorService;
    }

    @PostMapping("/load")
    @Override
    public String loadModel(@RequestParam String modelBinFile) {
        logger.info("Received request to load Model {}", modelBinFile);
        try {
            service.loadModel(modelBinFile);
            return "Model loaded successfully.";
        } catch (IOException e) {
            return "Error loading Model: " + e.getMessage();
        }
    }

    @GetMapping("/process-text")
    @Override
    public String processText(@RequestParam String text) {
        logger.info("Received request to predict language for: {}", text);
        return service.processText(text);
    }

    @PostMapping("/process-file")
    @Override
    public String processFile(@RequestParam String inputFile,@RequestParam String outputFile) {
        logger.info("Received request to process entries from file: {} and save results to: {}", inputFile, outputFile);
        try {
            return service.processFile(inputFile, outputFile);
        } catch (IOException e) {
            return "Error processing entries: " + e.getMessage();
        }
    }
    @GetMapping("/get-languages")
    public String getLanguages() {
        logger.info("Received request to get model languages");
        return service.getLanguages();
    }

}
