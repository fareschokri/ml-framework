package com.mlframework.service.impl;

import com.mlframework.dataaccess.FileDataAccess;
import com.mlframework.model.EntryLine;
import com.mlframework.service.itf.ModelService;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class TokenizerService implements ModelService {
    TokenizerModel tokenizerModel;
    private static final Logger logger = LoggerFactory.getLogger(TokenizerService.class);

    @Override
    public void loadModel(String modelFile) throws IOException {
        logger.info("Loading model from file: {}", modelFile);
        InputStream modelIn = new FileInputStream(modelFile);
        tokenizerModel = new TokenizerModel(modelIn);
        logger.info("Model loaded successfully.");

    }

    @Override
    public String processText(String text) {
        if (tokenizerModel == null) {
            return NO_MODEL_LOADED;
        }
        Tokenizer tokenizer = new TokenizerME(tokenizerModel);
        return Arrays.toString(tokenizer.tokenize(text));
    }

    @Override
    public String processFile(String inputFile, String outputFile) throws IOException {
        if (tokenizerModel == null) {
            return NO_MODEL_LOADED;
        } else{
            logger.info("Processing entries from file: {}", inputFile);
            List<EntryLine> entryLines = FileDataAccess.getEntriesFromFile(inputFile);
            for (EntryLine entryLine : entryLines) {
                entryLine.setLabel(processText(entryLine.getText()));
            }
            FileDataAccess.writeResultsToFile(entryLines, outputFile);
            return "Entries processed and results saved to: "+ outputFile;
        }
    }
}
