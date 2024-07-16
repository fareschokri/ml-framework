package com.mlframework.service.impl;

import com.mlframework.dataaccess.FileDataAccess;
import com.mlframework.model.EntryLine;
import com.mlframework.service.itf.ModelService;
import opennlp.tools.langdetect.*;
import opennlp.tools.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LanguageDetectorService implements ModelService {

    private LanguageDetector categorizer;
    private static final Logger logger = LoggerFactory.getLogger(LanguageDetectorService.class);

    @Override
    public void trainModel(Object...params) throws IOException {
        if (params.length != 5) {
            throw new IllegalArgumentException("Expected 5 parameters: trainingDataFile, modelBinOutput, algorithm, cutoff, iterations");
        }
        String trainingDataFile = (String) params[0];
        String modelBinOutput = (String) params[1];
        String algorithm = (String) params[2];
        int cutoff = (int) params[3];
        int iterations = (int) params[4];
        if (!VALID_ALGORITHMS.contains(algorithm.toUpperCase())) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm+
                    " Should be one of:\n"+VALID_ALGORITHMS);
        }

        logger.info("Starting model training with data file: {}", trainingDataFile);
        InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(trainingDataFile));
        ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");

        ObjectStream<LanguageSample> sampleStream = new LanguageDetectorSampleStream(lineStream);

        TrainingParameters trainingParameters = new TrainingParameters();
        trainingParameters.algorithm(algorithm);
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));

        LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, trainingParameters,  new LanguageDetectorFactory());
        categorizer = new LanguageDetectorME(model);
        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelBinOutput));
        model.serialize(modelOut);
        modelOut.close();

    }
    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
        categorizer = new LanguageDetectorME(model);
        logger.info("Model loaded successfully.");
    }

    @Override
    public String processText(String text){
        if (categorizer == null )
            return NO_MODEL_LOADED;
        logger.info("Predicting language for text: {}", text);
        Language bestLanguage = categorizer.predictLanguage(text);
        Map<String,String> result = new HashMap<>();
        result.put("Best language",bestLanguage.getLang());
        result.put("Best language confidence", Double.toString(bestLanguage.getConfidence()));
        return result.toString();
    }

    @Override
    public String processFile(String inputFile, String outputFile) throws IOException {
        if (categorizer == null) {
            return NO_MODEL_LOADED;
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = FileDataAccess.getEntriesFromFile(inputFile);
            for (EntryLine entryLine : entryLines) {
                entryLine.setLabel(categorizer.predictLanguage(entryLine.getText()).getLang());
            }
            FileDataAccess.writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }
    public String getLanguages(){
        if (categorizer == null )
                return NO_MODEL_LOADED;
        logger.info("Getting all model languages");
        Language[] languages = categorizer.predictLanguages("");
        return Arrays.toString(languages);
    }

}
