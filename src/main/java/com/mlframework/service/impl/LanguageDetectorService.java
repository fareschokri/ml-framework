package com.mlframework.service.impl;

import com.mlframework.dataaccess.FileDataAccess;
import com.mlframework.model.EntryLine;
import com.mlframework.service.itf.ModelService;
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
import java.util.List;
import java.util.Map;

@Service
public class LanguageDetectorService implements ModelService {

    private LanguageDetector categorizer;
    private static final Logger logger = LoggerFactory.getLogger(LanguageDetectorService.class);

    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        LanguageDetectorModel model = new LanguageDetectorModel(modelIn);
        categorizer = new LanguageDetectorME(model);
        logger.info("Model loaded successfully.");
    }

    @Override
    public String processText(String text){
        if (categorizer == null )
            return NO_MODEL_LOADED;
        logger.info("Predicting language for text: {}", text);
        Language bestLanguage = categorizer.predictLanguage(text);
        Map<String,String> result = new HashMap<>();
        result.put("Best language",bestLanguage.getLang());
        result.put("Best language confidence", Double.toString(bestLanguage.getConfidence()));
        return result.toString();
    }

    @Override
    public String processFile(String inputFile, String outputFile) throws IOException {
        if (categorizer == null) {
            return NO_MODEL_LOADED;
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = FileDataAccess.getEntriesFromFile(inputFile);
            for (EntryLine entryLine : entryLines) {
                entryLine.setLabel(categorizer.predictLanguage(entryLine.getText()).getLang());
            }
            FileDataAccess.writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }
    public String getLanguages(){
        if (categorizer == null )
                return NO_MODEL_LOADED;
        logger.info("Getting all model languages");
        Language[] languages = categorizer.predictLanguages("");
        return Arrays.toString(languages);
    }

}
