package com.mlframework.controller.impl;


import com.mlframework.controller.itf.ModelController;
import com.mlframework.service.impl.DocCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/doccat")
public class DocCatController implements ModelController {

    private static final Logger logger = LoggerFactory.getLogger(DocCatController.class);

    private final DocCatService service;

    @Autowired
    private DocCatController(DocCatService docCatService){
        this.service = docCatService;
    }

    @PostMapping("/train")
    public String trainModel(@RequestParam String trainingDataFile,
                             @RequestParam(defaultValue = "${mlframework.doccat.output}") String modelBinOutput,
                             @RequestParam(defaultValue = "${mlframework.doccat.languageCode}") String languageCode,
                             @RequestParam(defaultValue = "${mlframework.doccat.algorithm}") String algorithm,
                             @RequestParam(defaultValue = "${mlframework.doccat.cutoff}") String cutoff) {
        logger.info("Received request to train model with data file: {}", trainingDataFile);
        try {
            service.trainModel(trainingDataFile, languageCode, modelBinOutput, algorithm, Integer.parseInt(cutoff));
            return "Model trained and saved successfully.";
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

    @GetMapping("/process-text")
    @Override
    public String processText(@RequestParam String text) {
        logger.info("Received request to get all outcomes and their probabilities for: {}", text);
        return service.processText(text);
    }
}

