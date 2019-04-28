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
    
    private static final String DUMP_DIRECTORY = "/home/wikidata/";
    private static final String TMP_DIRECTORY = "/tmp/";
    
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
        
        LOGGER.info("Starting...");
        
        System.setProperty("user.dir", DUMP_DIRECTORY);
        DumpProcessingController dumpProcessingController = new DumpProcessingController("wikidatawiki");
        
        MwDumpFile dumpFile = null;
        if (args.length == 0) {
            dumpProcessingController.setOfflineMode(false);
            dumpFile = dumpProcessingController.getMostRecentDump(DumpContentType.JSON);
        } else if (args.length == 1) {
            String date = args[0];
            dumpProcessingController.setOfflineMode(false);
            dumpFile = new MwLocalDumpFile(DUMP_DIRECTORY + "dumpfiles/wikidatawiki/json-" + date + "/" + date + ".json.gz", DumpContentType.JSON, date, "wikidatawiki");
        } else {
            System.out.println("Invalid number of arguments.");
        }
        
        if (dumpFile != null) {
            
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
                dumpDateStamp = dumpDateStamp.substring(0, 4) + "-" + dumpDateStamp.substring(4, 6) + "-" + dumpDateStamp.substring(6, 8);
                dumpFW.write(dumpDateStamp);
                
                dumpProcessingController.registerEntityDocumentProcessor(new PropertiesProcessor(property, properties), null, true);
                
                dumpProcessingController.registerEntityDocumentProcessor(new ProjectsProcessor(project, projects), null, true);
                
                try {
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
                e.printStackTrace();
            }
            
        }
        
        LOGGER.info("Finished.");
        
    }
    
}
