package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.*;

public class ACityLayouter {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityRepository repository;

    public ACityLayouter(ACityRepository aCityRepository,SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
    }


    public void layoutRepository(){

        Collection<ACityElement> buildings = repository.getElementsByType(ACityElement.ACityType.Building);
        for (ACityElement building: buildings) {
            layoutBuilding(building);
        }

        Collection<ACityElement> buildingDistricts = getParentDistricts(buildings);
        for(ACityElement buildingDistrict : buildingDistricts){
            layoutDistrict(buildingDistrict);
        }


        //TODO recursiv

        Collection<ACityElement> districts = getParentDistricts(buildingDistricts);
        for(ACityElement district : districts){
            layoutDistrict(district);
        }



    }


    private Collection<ACityElement> getParentDistricts(Collection<ACityElement> elements) {
        Map<String, ACityElement> parentDistricts = new HashMap<>();
        for(ACityElement element : elements){

            ACityElement parentElement = element.getParentElement();
            if(parentElement == null){
                continue;
            }

            String hash = parentElement.getHash();
            if(!parentDistricts.containsKey(hash)){
                parentDistricts.put(hash, parentElement);
            }
        }
        return parentDistricts.values();
    }

    private void layoutBuilding(ACityElement building) {
        Collection<ACityElement> floors = building.getSubElementsOfType(ACityElement.ACityType.Floor);
        Collection<ACityElement> chimneys = building.getSubElementsOfType(ACityElement.ACityType.Chimney);

        ACityBuildingLayout buildingLayout = new ACityBuildingLayout(building, floors, chimneys, config);
        buildingLayout.calculate();
    }

    private void layoutDistrict(ACityElement buildingDistrict) {
        Collection<ACityElement> buildings = buildingDistrict.getSubElements();

        ACityDistrictLayout aCityDistrictLayout = new ACityDistrictLayout(buildingDistrict,  buildings, config);
        aCityDistrictLayout.calculate();
    }

}
