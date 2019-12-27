package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.Collection;

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
        layoutBuildings(buildings);

    }

    private void layoutBuildings(Collection<ACityElement> buildings) {

        for (ACityElement building: buildings) {
            Collection<ACityElement> floors = building.getSubElementsOfType(ACityElement.ACityType.Floor);
            Collection<ACityElement> chimneys = building.getSubElementsOfType(ACityElement.ACityType.Chimney);

            ACityBuildingLayout buildingLayout = new ACityBuildingLayout(building, floors, chimneys, config);
            buildingLayout.calculate();
        }

    }

}
