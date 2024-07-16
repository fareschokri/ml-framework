package com.mlframework.nlp.controller.itf;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * Interface for Apache OpenNLP model controllers that provides REST endpoints for training, loading, and using models.
 */
public interface ModelController {

    /**
     *
     * Trains a model with the provided input.
     *
     * @param trainingDataFile File containing training data, respecting the equivalent model Apache OpenNLP format.
     *                         Format details can be found at <a href="https://opennlp.apache.org/docs/2.3.3/manual/opennlp.html"></a>
     * @param modelBinOutput Bin file to write the trained model.
     * @param algorithm algorithm to use, one of "MAXENT", "PERCEPTRON", "NAIVEBAYES"
     * @param cutoff cutoff parameter value
     * @param iterations number of iterations to run
     * @param languageCode languageCode when applicable
     * @return a string : success when model trained, loaded and saved. Error otherwise.
     * @throws IOException if an I/O error occurs during training
     */
    String trainModel(@RequestParam String trainingDataFile,
                 @RequestParam String modelBinOutput,
                 @RequestParam String algorithm,
                 @RequestParam String cutoff,
                 @RequestParam String iterations,
                 @RequestParam String languageCode) throws IOException;

    /**
     *
     * Loads a model from the specified Bin file.
     *
     * @param modelBinFile Model Bin file to load
     * @return String: success when model loaded, error otherwise.
     */
    String loadModel(@RequestParam String modelBinFile);

    /**
     *
     * Processes entries from an input file and saves the results to an output file.
     *
     * @param inputFile input file containing lines to be processed by the model.
     * @param outputFile output file to write the result,
     * @return Success message with output file path when processed, error and cause otherwise.
     */

    String processFile(@RequestParam String inputFile,@RequestParam String outputFile);

    /**
     *
     * Processes a text and returns model processing result.
     *
     * @param text to be processed by the model.
     * @return processing result.
     */
    String processText(@RequestParam String text);
}
