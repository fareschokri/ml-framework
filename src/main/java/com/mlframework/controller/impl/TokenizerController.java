package com.mlframework.controller.impl;

import com.mlframework.controller.itf.ModelController;
import com.mlframework.service.impl.TokenizerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/tokenizer")
public class TokenizerController implements ModelController {

    private final TokenizerService service;
    private static final Logger logger = LoggerFactory.getLogger(TokenizerController.class);

    @Autowired
    private TokenizerController(TokenizerService tokenizerService){this.service = tokenizerService;}

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
        logger.info("Received request to tokenize: {}", text);
        return service.processText(text);
    }

    @PostMapping("/process-file")
    @Override
    public String processFile(@RequestParam String inputFile, @RequestParam String outputFile) {
        logger.info("Received request to process entries from file: {} and save results to: {}", inputFile, outputFile);
        try {
            return service.processFile(inputFile, outputFile);
        } catch (IOException e) {
            return "Error processing entries: " + e.getMessage();
        }
    }

}
