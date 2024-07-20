package com.mlframework.nlp.controller.impl;


import com.mlframework.nlp.controller.itf.ModelController;
import com.mlframework.nlp.service.impl.DoccatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/doccat")
public class DoccatController implements ModelController {

    private static final Logger logger = LoggerFactory.getLogger(DoccatController.class);

    private final DoccatService service;

    /**
     * Constructs a new {@code DoccatController} with the given service.
     *
     * @param doccatService the document categorizer service
     */
    @Autowired
    private DoccatController(DoccatService doccatService){
        this.service = doccatService;
    }

    @PostMapping("/train")
    @Override
    public String trainModel(@RequestParam String trainingDataFile,
                             @RequestParam(defaultValue = "${mlframework.doccat.output}") String modelBinOutput,
                             @RequestParam(defaultValue = "${mlframework.doccat.algorithm}") String algorithm,
                             @RequestParam(defaultValue = "${mlframework.doccat.cutoff}") String cutoff,
                             @RequestParam(defaultValue = "${mlframework.doccat.iterations}") String iterations,
                             @RequestParam(defaultValue = "${mlframework.doccat.languageCode}") String languageCode,
                             @RequestParam(required = false) String unusedFinderType) {
        logger.info("Received request to train model with data file: {}", trainingDataFile);
        try {
            service.trainModel(trainingDataFile, modelBinOutput, algorithm,
                    Integer.parseInt(cutoff), Integer.parseInt(iterations), languageCode);
            return "Model trained, loaded and successfully saved to "+ modelBinOutput;
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

    @GetMapping("/process-text")
    @Override
    public String processText(@RequestParam String text) {
        logger.info("Received request to get all outcomes and their probabilities for: {}", text);
        return service.processText(text);
    }
}

