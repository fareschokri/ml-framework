package com.mlframework.nlp.controller.impl;

import com.mlframework.nlp.controller.itf.ModelController;
import com.mlframework.nlp.service.impl.TokenizerService;
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

    /**
     * Constructs a new {@code TokenizerController} with the given service.
     *
     * @param tokenizerService the tokenizer service
     */
    @Autowired
    private TokenizerController(TokenizerService tokenizerService){this.service = tokenizerService;}

    @PostMapping("/train")
    @Override
    public String trainModel(@RequestParam String trainingDataFile,
                             @RequestParam(defaultValue = "${mlframework.tokenizer.output}") String modelBinOutput,
                             @RequestParam(defaultValue = "${mlframework.tokenizer.algorithm}") String algorithm,
                             @RequestParam(defaultValue = "${mlframework.tokenizer.cutoff}") String cutoff,
                             @RequestParam(defaultValue = "${mlframework.tokenizer.iterations}") String iterations,
                             @RequestParam(defaultValue = "${mlframework.tokenizer.languageCode}") String languageCode) {
        logger.info("Received request to train model with data file: {}", trainingDataFile);
        try {
            service.trainModel(trainingDataFile, modelBinOutput, algorithm,
                    Integer.parseInt(cutoff), Integer.parseInt(iterations), languageCode);
            return "Model trained, loaded and saved successfully to "+ modelBinOutput;
        } catch (IOException e) {
            logger.error("Error training model", e);
            return "Error training model: " + e.getMessage();
        }
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
        logger.info("Received request to tokenize: {}", text);
        return service.processText(text);
    }

    @GetMapping("/process-file")
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
