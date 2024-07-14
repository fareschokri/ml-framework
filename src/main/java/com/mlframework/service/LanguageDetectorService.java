package com.mlframework.service;

import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class LanguageDetectorService {

    private LanguageDetector categorizer;
    private static final Logger logger = LoggerFactory.getLogger(LanguageDetectorService.class);

    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
        categorizer = new LanguageDetectorME(model);
        logger.info("Model loaded successfully.");
    }

    public String predictLanguage(String text){
        if (categorizer == null )
            return "No Model loaded";
        logger.info("Predicting language for text: {}", text);
        Language bestLanguage = categorizer.predictLanguage(text);
        Map<String,String> result = new HashMap<>();
        result.put("Best language",bestLanguage.getLang());
        result.put("Best language confidence", Double.toString(bestLanguage.getConfidence()));
        return result.toString();
    }

    public String getLanguages(){
        if (categorizer == null )
                return "No Model loaded";
        logger.info("Getting all model languages");
        Language[] languages = categorizer.predictLanguages("");
        return Arrays.toString(languages);
    }

}
