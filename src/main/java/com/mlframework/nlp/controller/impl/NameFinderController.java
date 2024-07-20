package com.mlframework.nlp.controller.impl;

import com.mlframework.nlp.controller.itf.ModelController;
import com.mlframework.nlp.service.impl.NameFinderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/name-finder")
public class NameFinderController implements ModelController {

    private final NameFinderService service;
    private static final Logger logger = LoggerFactory.getLogger(NameFinderController.class);

    /**
     * Constructs a new {@code NameFinderController} with the given service.
     *
     * @param nameFinderService the name finder service
     */
    @Autowired
    private NameFinderController(NameFinderService nameFinderService){this.service = nameFinderService;}

    @PostMapping("/train")
    @Override
    public String trainModel(@RequestParam String trainingDataFile,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.output}") String modelBinOutput,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.algorithm}") String algorithm,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.cutoff}") String cutoff,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.iterations}") String iterations,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.languageCode}") String languageCode,
                             @RequestParam(defaultValue = "${mlframework.nameFinder.finderType}") String finderType) {
        logger.info("Received request to train model with data file: {}", trainingDataFile);
        try {
            service.trainModel(trainingDataFile, modelBinOutput, algorithm,
                    Integer.parseInt(cutoff), Integer.parseInt(iterations), languageCode, finderType);
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
        logger.info("Received request to process: {}", text);
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
