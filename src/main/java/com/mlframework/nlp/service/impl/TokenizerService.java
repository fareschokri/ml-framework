package com.mlframework.nlp.service.impl;

import com.mlframework.nlp.dataaccess.FileDataAccess;
import com.mlframework.nlp.model.EntryLine;
import com.mlframework.nlp.service.itf.ModelService;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;

@Service
public class TokenizerService implements ModelService {
    TokenizerModel model;
    private static final Logger logger = LoggerFactory.getLogger(TokenizerService.class);

    @Override
    public void trainModel(Object...params) throws IOException {
        if (params.length != 6) {
            throw new IllegalArgumentException("Expected 6 parameters: trainingDataFile, " +
                    "modelBinOutput, algorithm, cutoff, iterations and languageCode");
        }
        String trainingDataFile = (String) params[0];
        String modelBinOutput = (String) params[1];
        String algorithm = (String) params[2];
        int cutoff = (int) params[3];
        int iterations = (int) params[4];
        String lang = (String) params[5];
        if (!VALID_ALGORITHMS.contains(algorithm.toUpperCase())) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm+
                    " Should be one of:\n"+VALID_ALGORITHMS);
        }

        logger.info("Starting model training with data file: {}", trainingDataFile);
        InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(trainingDataFile));
        ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");

        ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);

        TrainingParameters trainingParameters = new TrainingParameters();
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));

        try {
            model = TokenizerME.train(sampleStream,
                    TokenizerFactory.create(null, lang, null, true, null), TrainingParameters.defaultParams());
        }
        finally {
            sampleStream.close();
        }

        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelBinOutput));
        model.serialize(modelOut);
        modelOut.close();

        logger.info("Model training completed and successfully saved to {}.", modelBinOutput);

    }
    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        model = new TokenizerModel(modelIn);
        logger.info("Model loaded successfully.");

    }

    @Override
    public String processText(String text) {
        if (model == null) {
            return NO_MODEL_LOADED;
        }
        Tokenizer tokenizer = new TokenizerME(model);
        return Arrays.toString(tokenizer.tokenize(text));
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
