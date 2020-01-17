package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.Collection;

public class ACityDesigner {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityRepository repository;

    public ACityDesigner(ACityRepository aCityRepository,SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
    }

    public void designRepository(){

        Collection<ACityElement> floors = repository.getElementsByType(ACityElement.ACityType.Floor);
        for (ACityElement floor: floors) {
            designFloor(floor);
        }

        Collection<ACityElement> chimneys = repository.getElementsByType(ACityElement.ACityType.Chimney);
        for (ACityElement chimney: chimneys) {
            designChimney(chimney);
        }

        Collection<ACityElement> buildings = repository.getElementsByType(ACityElement.ACityType.Building);
        for (ACityElement building: buildings) {
            designBuildings(building);
        }

        Collection<ACityElement> districts = repository.getElementsByType(ACityElement.ACityType.District);
        for (ACityElement district: districts) {
            designDistrict(district);
        }
    }



    private void designDistrict(ACityElement district) {
        district.setColor("#FE2EF7"); //TODO Config
        district.setShape(ACityElement.ACityShape.box); //TODO Config
    }

    private void designBuildings(ACityElement building) {
        building.setColor("#2ECCFA"); //TODO Config
        building.setShape(ACityElement.ACityShape.box); //TODO Config
    }

    private void designChimney(ACityElement chimney) {
        chimney.setColor("#FFFF00"); //TODO Config
        chimney.setShape(ACityElement.ACityShape.cylinder); //TODO Config
    }

    private void designFloor(ACityElement floor) {
        floor.setColor("#013ADF"); //TODO Config
        floor.setShape(ACityElement.ACityShape.box); //TODO Config
    }



}
