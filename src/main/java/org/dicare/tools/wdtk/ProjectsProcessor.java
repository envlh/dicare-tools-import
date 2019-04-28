package org.dicare.tools.wdtk;

import java.util.HashMap;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;

public class ProjectsProcessor implements EntityDocumentProcessor {
    
    private HashMap<String, Long> project;
    private HashMap<String, HashMap<String, Long>> projects;
    
    //private int count = 0;
    
    public ProjectsProcessor(HashMap<String, Long> project, HashMap<String, HashMap<String, Long>> projects) {
        this.project = project;
        this.projects = projects;
    }
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
        Set<String> usedProjects = itemDocument.getSiteLinks().keySet();
        for (String idA : usedProjects) {
            incProject(idA);
            for (String idB : usedProjects) {
                if (idA.compareTo(idB) < 0) {
                    incProjects(idA, idB);
                }
            }
        }
        /*count++;
        if (count >= 10000) {
            throw new RuntimeException();
        }*/
    }
    
    private void incProject(String id) {
        if (!project.containsKey(id)) {
            project.put(id, 0L);
        }
        project.put(id, project.get(id) + 1L);
    }
    
    private void incProjects(String idA, String idB) {
        if (!projects.containsKey(idA)) {
            projects.put(idA, new HashMap<>());
        }
        if (!projects.get(idA).containsKey(idB)) {
            projects.get(idA).put(idB, 0L);
        }
        projects.get(idA).put(idB, projects.get(idA).get(idB) + 1L);
    }
    
}
