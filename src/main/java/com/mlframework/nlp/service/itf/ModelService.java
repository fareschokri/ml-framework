package com.mlframework.nlp.service.itf;

import java.io.IOException;
import java.util.Set;

/**
 * Interface for Apache OpenNLP model services that provides methods for training, loading, and using models.
 */
public interface ModelService {

    String NO_MODEL_LOADED = "No Model loaded, load one or train a new one.";
    Set<String> VALID_ALGORITHMS = Set.of("MAXENT", "PERCEPTRON", "NAIVEBAYES");


    /**
     * Trains a document categorization model.
     *
     * @param params params an array of parameters required for training the model. The specific parameters
     *               and their order depend on the implementation.
     * @throws IOException if an I/O error occurs during training
     * @throws IllegalArgumentException if the number of parameters is incorrect or if the algorithm is invalid
     */
    void trainModel(Object... params) throws IOException;

    /**
     * Loads a model from the specified Bin file.
     *
     * @param modelBinFile the file containing the model to be loaded
     * @throws IOException if an I/O error occurs during loading
     */
    void loadModel(String modelBinFile) throws IOException;

    /**
     * Processes entries from an input file and saves the results to an output file.
     *
     * @param inputFile  the input file containing the entries to be processed by the model
     * @param outputFile the output file to save the results
     * @return a message indicating the result of the processing operation
     * @throws IOException if an I/O error occurs during processing
     */
    String processFile(String inputFile, String outputFile) throws IOException;

    /**
     * Processes a text message and returns model processing result.
     *
     * @param text the text to be processed
     * @return a string representing the model output
     */
    String processText(String text);
}
