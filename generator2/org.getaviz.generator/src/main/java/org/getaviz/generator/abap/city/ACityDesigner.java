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

        district.setShape(ACityElement.ACityShape.box); //TODO Config

        if (district.getSourceNode() == null){
            switch (district.getSubType()){
                case Class:         district.setColor("#F4D03F"); break; //TODO Config
                case Report:        district.setColor("#85C1E9"); break; //TODO Config
                case FunctionGroup: district.setColor("#7D3C98"); break; //TODO Config
                case Table:         district.setColor("#1A5276"); break; //TODO Config
                case DDIC:          district.setColor("#229954"); break; //TODO Config
            }
        } else {
            district.setColor("#95A5A6"); //TODO Config
        }
    }

    private void designBuildings(ACityElement building) {

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Class.name())){
            building.setColor("#ff00ff"); //TODO Config
            building.setShape(ACityElement.ACityShape.box); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Interface.name())){
            building.setColor("#ff0000"); //TODO Config
            building.setShape(ACityElement.ACityShape.box); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Report.name())){
            building.setColor("#00007f"); //TODO Config
            building.setShape(ACityElement.ACityShape.box); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.FunctionGroup.name())){
            building.setColor("#7f003f"); //TODO Config
            building.setShape(ACityElement.ACityShape.box); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Table.name())){
            building.setColor("#bf5f00"); //TODO Config
            building.setShape(ACityElement.ACityShape.cylinder); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }


        if (building.getSourceNode().hasLabel(SAPNodeLabels.DataElement.name())){
            building.setColor("#00bf00"); //TODO Config
            building.setShape(ACityElement.ACityShape.box); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Domain.name())){
            building.setColor("#176817"); //TODO Config
            building.setShape(ACityElement.ACityShape.cone); //TODO Config
            building.setWidth(building.getWidth() - 0.1); //TODO Config
            building.setLength(building.getLength() - 0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.Structure.name())){
            building.setColor("#e0e08f"); //TODO Config
            building.setShape(ACityElement.ACityShape.cylinder); //TODO Config
            building.setWidth(0.1); //TODO Config
            building.setLength(0.1); //TODO Config
        }

        if (building.getSourceNode().hasLabel(SAPNodeLabels.TableType.name())){
            building.setColor("#ff7f00"); //TODO Config
            building.setShape(ACityElement.ACityShape.cylinder); //TODO Config
            building.setWidth(0.1); //TODO Config
            building.setLength(0.1); //TODO Config
        }


    }


    private void designChimney(ACityElement chimney) {
        chimney.setColor("#FFFF00"); //TODO Config
        chimney.setShape(ACityElement.ACityShape.cylinder); //TODO Config
    }


    private void designFloor(ACityElement floor) {

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.Method.name())){
            floor.setColor("#ffffff"); //TODO Config
            floor.setShape(ACityElement.ACityShape.box); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
        }

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.FormRoutine.name())){
            floor.setColor("#ffffff"); //TODO Config
            floor.setShape(ACityElement.ACityShape.box); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
        }

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.FunctionModule.name())){
            floor.setColor("#ffffff"); //TODO Config
            floor.setShape(ACityElement.ACityShape.box); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
        }

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.TableElement.name())){
            floor.setColor("#ffffff"); //TODO Config
            floor.setShape(ACityElement.ACityShape.cylinder); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
        }

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.StructureElement.name())){
            floor.setColor("#e0e08f"); //TODO Config
            floor.setShape(ACityElement.ACityShape.cone); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
        }

        if (floor.getSourceNode().hasLabel(SAPNodeLabels.DataElement.name())){
            floor.setColor("#00bf00"); //TODO Config
            floor.setShape(ACityElement.ACityShape.box); //TODO Config
            floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
            floor.setWidth(floor.getWidth() - 0.1); //TODO Config
            floor.setLength(floor.getLength() - 0.1); //TODO Config
        }
    }



}
