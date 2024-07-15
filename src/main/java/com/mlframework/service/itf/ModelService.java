package com.mlframework.service.itf;

import java.io.IOException;

public interface ModelService {

    String NO_MODEL_LOADED = "No Model loaded, load one or train a new one.";
    void loadModel(String modelBinFile) throws IOException;
    String processFile(String inputFile, String outputFile) throws IOException;
    String processText(String text);
}
