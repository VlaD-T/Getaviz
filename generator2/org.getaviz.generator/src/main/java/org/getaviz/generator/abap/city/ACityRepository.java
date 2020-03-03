package org.getaviz.generator.abap.city;

import org.apache.commons.codec.digest.DigestUtils;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;

import java.util.*;

import static org.getaviz.generator.abap.city.ACityElement.ACitySubType.*;
import static org.getaviz.generator.abap.city.ACityElement.ACityType.Building;
import static org.getaviz.generator.abap.city.ACityElement.ACityType.District;

public class ACityRepository {

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

    public Collection<ACityElement> getElementsByTypeAndSourceProperty(ACityElement.ACityType type, String sourceProperty, String sourcePropertyValue){
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

            Value propertyValue = sourceNode.get(sourceProperty);
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
