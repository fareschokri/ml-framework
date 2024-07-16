package com.mlframework.controller.itf;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

public interface ModelController {
    String trainModel(@RequestParam String trainingDataFile,
                 @RequestParam String modelBinOutput,
                 @RequestParam String algorithm,
                 @RequestParam String cutoff,
                 @RequestParam String iterations,
                 @RequestParam String languageCode) throws IOException;

    String loadModel(@RequestParam String modelBinFile);
    String processFile(@RequestParam String inputFile,@RequestParam String outputFile);
    String processText(@RequestParam String text);
}
