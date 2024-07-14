package com.mlframework.service;

import opennlp.tools.doccat.*;
import opennlp.tools.util.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import com.mlframework.model.EntryLine;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;


@Service
public class DocCatService {

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

    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        model = new DoccatModel(modelIn);
        categorizer = new DocumentCategorizerME(model);
        logger.info("Model loaded successfully.");
    }

    public String classifyEntries(String inputFile, String outputFile) throws IOException {
        if (model == null) {
            return "No Model loaded";
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = getEntries(inputFile);
            doClassify(entryLines);
            writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }

    private List<EntryLine> getEntries(String inputFile) throws IOException {
        logger.info("Reading entries from file: {}", inputFile);
        List<EntryLine> entryLines = new ArrayList<>();
        try (LineIterator iterator = FileUtils.lineIterator(new File(inputFile), "UTF-8")){
            while (iterator.hasNext()) {
                String line = iterator.nextLine();
                String[] parts = line.split(",", 2);
                EntryLine entryLine = new EntryLine();
                entryLine.setText(parts[0]);
                entryLines.add(entryLine);
            }
        }
        logger.info("Total entries read: {}", entryLines.size());
        return entryLines;
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

    private void writeResultsToFile(List<EntryLine> entryLines, String outputFile) throws IOException {
        logger.info("Writing results to file: {}", outputFile);
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.append("Text,Classification\n");
            for (EntryLine entryLine : entryLines) {
                writer.append(entryLine.getText()).append(",")
                        .append(entryLine.getLabel()).append("\n");
            }
        }
        logger.info("Results written to file successfully.");
    }

    public String getTextOutcomes(String message) {
        if (model == null) {
            return "No model loaded";
        } else{
            double [] testEval = model.getMaxentModel().eval(message.split(""));
            return model.getMaxentModel().getAllOutcomes(testEval);
        }
    }
}
