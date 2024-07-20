package com.mlframework.nlp.service.impl;

import com.mlframework.nlp.dataaccess.FileDataAccess;
import com.mlframework.nlp.model.EntryLine;
import com.mlframework.nlp.service.itf.ModelService;
import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class NameFinderService implements ModelService {
    TokenNameFinderModel model;

    private static final Logger logger = LoggerFactory.getLogger(NameFinderService.class);

    @Override
    public void trainModel(Object...params) throws IOException {
        if (params.length != 7) {
            throw new IllegalArgumentException("Expected 6 parameters: trainingDataFile, " +
                    "modelBinOutput, algorithm, cutoff, iterations, languageCode and finderType");
        }
        String trainingDataFile = (String) params[0];
        String modelBinOutput = (String) params[1];
        String algorithm = (String) params[2];
        int cutoff = (int) params[3];
        int iterations = (int) params[4];
        String lang = (String) params[5];
        String finderType = (String) params[6];
        if (!VALID_ALGORITHMS.contains(algorithm.toUpperCase())) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm+
                    " Should be one of:\n"+VALID_ALGORITHMS);
        }

        logger.info("Starting model training with data file: {}", trainingDataFile);
        InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(trainingDataFile));
        try (ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8")) {

            TrainingParameters trainingParameters = new TrainingParameters();
            trainingParameters.algorithm(algorithm);
            trainingParameters.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
            trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));

            TokenNameFinderFactory factory = TokenNameFinderFactory.create(null, null,
                    Collections.emptyMap(), new BioCodec());

            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
            model = NameFinderME.train(lang, finderType, sampleStream, trainingParameters, factory);
            OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelBinOutput));
            model.serialize(modelOut);

        }

        logger.info("Model training completed and successfully saved to {}.", modelBinOutput);
    }
    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        model = new TokenNameFinderModel(modelIn);
        logger.info("Model loaded successfully.");

    }

    @Override
    public String processText(String text) {
        if (model == null) {
            return NO_MODEL_LOADED;
        }
        NameFinderME nameFinder = new NameFinderME(model);
        String[] tokens = text.split("\\s+");
        Span[] nameSpans = nameFinder.find(tokens);

        return Arrays.toString(nameSpans);
    }

    @Override
    public String processFile(String inputFile, String outputFile) throws IOException {
        if (model == null) {
            return NO_MODEL_LOADED;
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = FileDataAccess.getEntriesFromFile(inputFile);
            for (EntryLine entryLine : entryLines) {
                entryLine.setLabel(processText(entryLine.getText()));
            }
            FileDataAccess.writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }
}
