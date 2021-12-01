package org.dicare.tools.wdtk;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private static final String TMP_DIRECTORY = "/data/project/dicare/dicare-tools/tmp/";

    public static void configureLogging() {
        ConsoleAppender consoleAppender = new ConsoleAppender();
        String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
        consoleAppender.setLayout(new PatternLayout(pattern));
        consoleAppender.setThreshold(Level.DEBUG);
        consoleAppender.activateOptions();
        Logger.getRootLogger().addAppender(consoleAppender);
    }

    public static void main(String[] args) {

        configureLogging();

        if (args.length != 1) {
            System.out.println("Usage: java -jar wdtk-import.jar <path to Wikidata JSON dump>");
            System.exit(1);
        }

        LOGGER.info("WDTK initialization...");

        String filePath = args[0];
        DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
        dumpProcessingController.setOfflineMode(true);
        MwDumpFile dumpFile = new MwLocalDumpFile(filePath, DumpContentType.JSON, null, "wikidatawiki");

        HashMap<Long, Long> property = new HashMap<>();
        HashMap<Long, HashMap<Long, Long>> properties = new HashMap<>();

        HashMap<String, Long> project = new HashMap<>();
        HashMap<String, HashMap<String, Long>> projects = new HashMap<>();

        try (FileWriter dumpFW = new FileWriter(TMP_DIRECTORY + "dump.csv");
                BufferedWriter dumpBW = new BufferedWriter(dumpFW);
                FileWriter propertyFW = new FileWriter(TMP_DIRECTORY + "property.csv");
                BufferedWriter propertyBW = new BufferedWriter(propertyFW);
                FileWriter propertiesFW = new FileWriter(TMP_DIRECTORY + "properties.csv");
                BufferedWriter propertiesBW = new BufferedWriter(propertiesFW);
                FileWriter projectFW = new FileWriter(TMP_DIRECTORY + "project.csv");
                BufferedWriter projectBW = new BufferedWriter(projectFW);
                FileWriter projectsFW = new FileWriter(TMP_DIRECTORY + "projects.csv");
                BufferedWriter projectsBW = new BufferedWriter(projectsFW)) {

            String dumpDateStamp = dumpFile.getDateStamp();
            dumpDateStamp = dumpDateStamp.substring(0, 4) + "-" + dumpDateStamp.substring(4, 6) + "-"
                    + dumpDateStamp.substring(6, 8);
            dumpFW.write(dumpDateStamp);

            dumpProcessingController.registerEntityDocumentProcessor(new PropertiesProcessor(property, properties),
                    null, true);

            dumpProcessingController.registerEntityDocumentProcessor(new ProjectsProcessor(project, projects), null,
                    true);

            try {
                LOGGER.info("WDTK processing...");
                dumpProcessingController.processDump(dumpFile);
            } catch (RuntimeException e) {
                // used for test only
            }

            // properties
            for (Long id : property.keySet()) {
                propertyFW.write(id + "," + property.get(id) + "\n");
            }
            for (Long idA : properties.keySet()) {
                for (Long idB : properties.get(idA).keySet()) {
                    propertiesFW.write(idA + "," + idB + "," + properties.get(idA).get(idB) + "\n");
                }
            }

            // projects
            for (String id : project.keySet()) {
                projectFW.write(id + "," + project.get(id) + "\n");
            }
            for (String idA : projects.keySet()) {
                for (String idB : projects.get(idA).keySet()) {
                    projectsFW.write(idA + "," + idB + "," + projects.get(idA).get(idB) + "\n");
                }
            }

        } catch (Exception e) {
            LOGGER.info("WDTK error: ", e);
            System.exit(2);
        }

        LOGGER.info("WDTK finished.");

    }

}
