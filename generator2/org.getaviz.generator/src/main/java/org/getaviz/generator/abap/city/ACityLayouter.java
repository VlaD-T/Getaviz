package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



    }


    private Collection<ACityElement> getParentDistricts(Collection<ACityElement> elements) {
        Map<String, ACityElement> parentDistricts = new HashMap<>();
        for(ACityElement element : elements){
            String hash = element.getHash();
            if(!parentDistricts.containsKey(hash)){
                parentDistricts.put(hash, element);
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
