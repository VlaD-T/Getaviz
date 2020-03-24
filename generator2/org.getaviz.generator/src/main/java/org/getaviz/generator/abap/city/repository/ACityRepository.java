package org.getaviz.generator.abap.city.repository;

import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;

import java.util.*;

public class ACityRepository {
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    private Map<Long, ACityElement> elementsBySourceID;

    private Map<String, ACityElement> elementsByHash;

    public ACityRepository(){
        elementsBySourceID = new HashMap<>();
        elementsByHash = new HashMap<>();
    }


    public Collection<ACityElement> getAllElements() {
        //TODO copy
        return elementsBySourceID.values();
    }

    public Collection<ACityElement> getAllElementsByHash() {
        //TODO copy
        return elementsByHash.values();
    }

    public ACityElement getElementBySourceID(Long sourceID){
        return elementsBySourceID.get(sourceID);
    }

    public ACityElement getElementByHash(String hash){
        return elementsByHash.get(hash);
    }

    public Collection<ACityElement> getElementsByType(ACityElement.ACityType type){
        List<ACityElement> elementsByType = new ArrayList<>();

        //TODO create Maps
        elementsByHash.forEach((id, element) -> {
            if(element.getType() == type) {
                elementsByType.add(element);
            }
        });

        return elementsByType;
    }

    public Collection<ACityElement> getElementsByTypeAndSourceProperty(ACityElement.ACityType type, SAPNodeProperties sourceProperty, String sourcePropertyValue){
        Collection<ACityElement> elementsByType = getElementsByType(type);
        List<ACityElement> elementsByTypeAndSourceProperty = new ArrayList<>();

        for (ACityElement element: elementsByType){

            Node sourceNode = element.getSourceNode();

            if(sourceNode == null ){
                ACityElement.ACitySubType subType = element.getSubType();
                String subTypeString = subType.toString();
                    if(!subTypeString.equals(sourcePropertyValue)){
                        continue;
                    }
                    elementsByTypeAndSourceProperty.add(element);

              continue;
            }

            Value propertyValue = sourceNode.get(sourceProperty.toString());
            if( propertyValue == null){
                continue;
            }

            String propertyValueString = propertyValue.asString();
            if(!propertyValueString.equals(sourcePropertyValue)){
                continue;
            }

            elementsByTypeAndSourceProperty.add(element);
        }

        return elementsByTypeAndSourceProperty;
    }

    //Test
    //Schreiben der ACityElemente in die Neo4j-Datenbank
    public void writeRepositoryToNeo4j() {

        elementsByHash.forEach((id, element) -> {
            //Prüfung auf bereits erstellte Nodes

            ACityElement.ACityType aCityElement = element.getType();
            connector.executeWrite("CREATE ( :Elements { " + getACityProperties(element, aCityElement) + "})");

        });
    }
    public void writeACityElementsToNeo4j(ACityElement.ACityType aCityElement){

        elementsByHash.forEach((id, element) -> {
            if(element.getType() == aCityElement){
                    connector.executeWrite("CREATE ( :Elements { " + getACityProperties(element, aCityElement) + "})");
            }
        });
    }

    private String getACityProperties(ACityElement element, ACityElement.ACityType aCityElement) {
        StringBuilder test = new StringBuilder();

        test.append(" cityType : '" + aCityElement.toString() + "',");
        test.append(" hash :  '"+ element.getHash() + "',");
        test.append(" subType :  '" + element.getSubType() + "',");
        test.append(" color :  '" + element.getColor() + "',");
        test.append(" shape :  '" + element.getShape() + "',");
        test.append(" height :  " + element.getHeight() + ",");
        test.append(" width :  " + element.getWidth() + ",");
        test.append(" length :  " + element.getLength() + ",");
        test.append(" height :  " + element.getHeight() + ",");
        test.append(" xPosition :  " + element.getXPosition() + ",");
        test.append(" yPosition :  " + element.getYPosition() + ",");
        test.append(" zPosition :  " + element.getZPosition() + "");


        return test.toString();
    }
    //Test Ende


    public void addElement(ACityElement element) {
        elementsByHash.put(element.getHash(), element);

        if (element.getSourceNode() != null){
            elementsBySourceID.put(element.getSourceNodeID(), element);
        }
    }

    public void addElements(List<ACityElement> elements) {
        for( ACityElement element : elements ){
            addElement(element);
        }
    }

}
