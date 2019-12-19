package org.getaviz.generator.abap.city;

import java.util.*;

public class ACityRepository {

    private Map<Long, ACityElement> elementsByID;

    public ACityRepository(){
        elementsByID = new HashMap<Long, ACityElement>();
    }


    public Collection<ACityElement> getAllElements() {
        //TODO copy
        return elementsByID.values();
    }

    public List<ACityElement> getElementsByType(ACityElement.ACityType type){
        List<ACityElement> elementsByType = new ArrayList<>();

        elementsByID.forEach((id, element) -> {
            if(element.getType() == type) {
                elementsByType.add(element);
            }
        });

        return elementsByType;
    }


    public void addElement(ACityElement element) {
        elementsByID.put(element.getSourceNodeID(), element);
    }

    public void addElements(List<ACityElement> elements) {
        for( ACityElement element : elements ){
            addElement(element);
        }
    }


}
