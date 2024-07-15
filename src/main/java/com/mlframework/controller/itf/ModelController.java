package com.mlframework.controller.itf;

public interface ModelController {
    String loadModel(String modelBinFile);
    String processFile(String inputFile, String outputFile);
    String processText(String text);
}
