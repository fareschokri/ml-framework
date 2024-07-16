package com.mlframework.service.itf;

import java.io.IOException;
import java.util.Set;

public interface ModelService {

    String NO_MODEL_LOADED = "No Model loaded, load one or train a new one.";
    Set<String> VALID_ALGORITHMS = Set.of("MAXENT", "PERCEPTRON", "NAIVEBAYES");


    void trainModel(Object... params) throws IOException;
    void loadModel(String modelBinFile) throws IOException;
    String processFile(String inputFile, String outputFile) throws IOException;
    String processText(String text);
}
