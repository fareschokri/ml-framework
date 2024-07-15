package com.mlframework.service.impl;

import com.mlframework.dataaccess.FileDataAccess;
import com.mlframework.service.itf.ModelService;
import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import com.mlframework.model.EntryLine;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


@Service
public class DocCatService implements ModelService {

    private DoccatModel model;
    private DocumentCategorizerME categorizer;

    private static final Logger logger = LoggerFactory.getLogger(DocCatService.class);

    private static final Set<String> VALID_ALGORITHMS = Set.of("MAXENT", "PERCEPTRON", "NAIVEBAYES");


    public void trainModel(String trainingDataFile, String lang, String modelBinOutput,
                           String algorithm, int cutoff) throws IOException {
        if (!VALID_ALGORITHMS.contains(algorithm.toUpperCase())) {
            throw new IllegalArgumentException("Invalid algorithm: " + algorithm+
                    " Should be one of:\n"+VALID_ALGORITHMS);
        }

        logger.info("Starting model training with data file: {}", trainingDataFile);
        InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(trainingDataFile));
        ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
        try(ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream))
        {
            TrainingParameters trainingParameters = new TrainingParameters();
            trainingParameters.algorithm(algorithm);
            trainingParameters.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));

            model = DocumentCategorizerME.train(lang, sampleStream, trainingParameters, new DoccatFactory());
            categorizer = new DocumentCategorizerME(model);
            OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelBinOutput));
            model.serialize(modelOut);
            modelOut.close();
        }
        logger.info("Model training completed and saved successfully to {}.", modelBinOutput);
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
