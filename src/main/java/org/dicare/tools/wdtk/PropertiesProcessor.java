package org.dicare.tools.wdtk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public class PropertiesProcessor implements EntityDocumentProcessor {
    
    private HashMap<Long, Long> property;
    private HashMap<Long, HashMap<Long, Long>> properties;
    
    //private int count = 0;
    
    public PropertiesProcessor(HashMap<Long, Long> property, HashMap<Long, HashMap<Long, Long>> properties) {
        this.property = property;
        this.properties = properties;
    }
    
    @Override
    public void processItemDocument(ItemDocument itemDocument) {
        processStatementDocument(itemDocument);
    }
    
    @Override
    public void processPropertyDocument(PropertyDocument propertyDocument) {
        processStatementDocument(propertyDocument);
    }
    
    @Override
    public void processLexemeDocument(LexemeDocument lexemeDocument) {
        processStatementDocument(lexemeDocument);
    }
    
    private void processStatementDocument(StatementDocument statementDocument) {
        TreeSet<Long> usedProperties = new TreeSet<>();
        for (Iterator<Statement> statements = statementDocument.getAllStatements(); statements.hasNext();) {
            Statement statement = statements.next();
            StatementRank rank = statement.getRank();
            if (!rank.equals(StatementRank.DEPRECATED)) {
                Value value = statement.getValue();
                if (value != null) {
                    usedProperties.add(Long.parseLong(statement.getClaim().getMainSnak().getPropertyId().getId().substring(1)));
                }
            }
        }
        for (Long idA : usedProperties) {
            incProperty(idA);
            for (Long idB : usedProperties) {
                if (idA < idB) {
                    incProperties(idA, idB);
                }
            }
        }
        /*count++;
        if (count >= 10000) {
            throw new RuntimeException();
        }*/
    }
    
    private void incProperty(Long id) {
        if (!property.containsKey(id)) {
            property.put(id, 0L);
        }
        property.put(id, property.get(id) + 1L);
    }
    
    private void incProperties(Long idA, Long idB) {
        if (!properties.containsKey(idA)) {
            properties.put(idA, new HashMap<>());
        }
        if (!properties.get(idA).containsKey(idB)) {
            properties.get(idA).put(idB, 0L);
        }
        properties.get(idA).put(idB, properties.get(idA).get(idB) + 1L);
    }
    
}
