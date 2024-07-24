package com.mlframework.nlp.service.impl;

import com.mlframework.nlp.dataaccess.FileDataAccess;
import com.mlframework.nlp.service.itf.ModelService;
import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import com.mlframework.nlp.model.EntryLine;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


@Service
public class DoccatService implements ModelService {

    private DoccatModel model;
    private DocumentCategorizerME categorizer;

    private static final Logger logger = LoggerFactory.getLogger(DoccatService.class);


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
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

        TrainingParameters trainingParameters = new TrainingParameters();
        trainingParameters.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        trainingParameters.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));

        model = DocumentCategorizerME.train(lang, sampleStream, trainingParameters, new DoccatFactory());
        categorizer = new DocumentCategorizerME(model);
        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelBinOutput));
        model.serialize(modelOut);
        modelOut.close();

        logger.info("Model training completed and successfully saved to {}.", modelBinOutput);
    }

    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        model = new DoccatModel(modelIn);
        categorizer = new DocumentCategorizerME(model);
        logger.info("Model loaded successfully.");
    }

    @Override
    public String processFile(String inputFile, String outputFile) throws IOException {
        if (model == null) {
            return NO_MODEL_LOADED;
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = FileDataAccess.getEntriesFromFile(inputFile);
            doClassify(entryLines);
            FileDataAccess.writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }


    /**
     * Classifies a list of entry lines using the loaded model.
     *
     * @param entryLines the list of entry lines to be classified
     */
    private void doClassify(List<EntryLine> entryLines) {
            logger.info("Classifying entry lines.");
            for (EntryLine entryLine : entryLines) {
                String text = entryLine.getText().toLowerCase();
                double[] outcomes = categorizer.categorize(text.split(""));
                String category = categorizer.getBestCategory(outcomes);
                entryLine.setLabel(category);
                logger.debug("Entry: {}, classified: {}", text, category);
            }
    }

    @Override
    public String processText(String message) {
        if (model == null) {
            return NO_MODEL_LOADED;
        } else{
            double [] testEval = model.getMaxentModel().eval(message.split(""));
            return model.getMaxentModel().getAllOutcomes(testEval);
        }
    }
}
