package com.mlframework.dataaccess;

import com.mlframework.model.EntryLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileDataAccess {

    private FileDataAccess(){}

    private static final Logger logger = LoggerFactory.getLogger(FileDataAccess.class);

    public static List<EntryLine> getEntriesFromFile(String inputFile) throws IOException {
        logger.info("Reading entries from file: {}", inputFile);
        List<EntryLine> entryLines = new ArrayList<>();
        try (LineIterator iterator = FileUtils.lineIterator(new File(inputFile), "UTF-8")){
            while (iterator.hasNext()) {
                EntryLine entryLine = new EntryLine();
                entryLine.setText(iterator.nextLine());
                entryLines.add(entryLine);
            }
        }
        logger.info("Total entries read: {}", entryLines.size());
        return entryLines;
    }

    public static void writeResultsToFile(List<EntryLine> entryLines, String outputFile) throws IOException {
        logger.info("Writing results to file: {}", outputFile);
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (EntryLine entryLine : entryLines) {
                writer.append(entryLine.getText()).append(",")
                        .append(entryLine.getLabel()).append("\n");
            }
        }
        logger.info("Results written to file successfully.");
    }
}
