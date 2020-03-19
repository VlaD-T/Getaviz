package org.getaviz.generator.abap.city.steps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.Collection;

import org.getaviz.generator.abap.city.ACityElement;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.repository.SAPNodeProperties;
import org.getaviz.generator.abap.city.repository.SAPNodeTypes;
import org.neo4j.driver.v1.types.Node;

public class ACityDesigner {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityRepository repository;

    public ACityDesigner(ACityRepository aCityRepository, SettingsConfiguration config) {
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

        district.setShape(config.getACityDistrictShape());

        if (district.getSourceNode() == null){
            switch (district.getSubType()){
                case Class:         district.setColor(config.getACityDistrictColorHex("classDistrict")); break;
                case Report:        district.setColor(config.getACityDistrictColorHex("reportDistrict")); break;
                case FunctionGroup: district.setColor(config.getACityDistrictColorHex("functionGroupDistrict")); break;
                case Table:         district.setColor(config.getACityDistrictColorHex("tableDistrict")); break;
                case DDIC:          district.setColor(config.getACityDistrictColorHex("dataDictionaryDistrict")); break;
            }
        } else {
            district.setColor(config.getACityDistrictColorHex("packageDistrict"));
        }
    }

    private void designBuildings(ACityElement building) {

        Node sourceNode = building.getSourceNode();



        if (sourceNode == null) {
            log.error("SourceNode nicht vorhnaden");
        } else {

            String propertyTypeName = sourceNode.get(String.valueOf(SAPNodeProperties.type_name)).asString();

            switch (SAPNodeTypes.valueOf(propertyTypeName)) {

                case Class:
                    building.setColor(config.getACityBuildingColorHex("classBuilding"));
                    building.setShape(config.getACityBuildingShape("classBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case Interface:
                    building.setColor(config.getACityBuildingColorHex("interfaceBuilding"));
                    building.setShape(config.getACityBuildingShape("interfaceBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case Report:
                    building.setColor(config.getACityBuildingColorHex("reportBuilding"));
                    building.setShape(config.getACityBuildingShape("reportBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case FunctionGroup:
                    building.setColor(config.getACityBuildingColorHex("functionGroupBuilding"));
                    building.setShape(config.getACityBuildingShape("functionGroupBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case Table:
                    building.setColor(config.getACityBuildingColorHex("tableBuilding"));
                    building.setShape(config.getACityBuildingShape("tableBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case DataElement:
                    building.setColor(config.getACityBuildingColorHex("dataElementBuilding"));
                    building.setShape(config.getACityBuildingShape("dataElementBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case Domain:
                    building.setColor(config.getACityBuildingColorHex("domainBuilding"));
                    building.setShape(config.getACityBuildingShape("domainBuilding"));
                    building.setWidth(building.getWidth() - 0.1); //TODO Config
                    building.setLength(building.getLength() - 0.1); //TODO Config
                    break;
                case Structure:
                    building.setColor(config.getACityBuildingColorHex("structureBuilding"));
                    building.setShape(config.getACityBuildingShape("structureBuilding"));
                    building.setWidth(0.1); //TODO Config
                    building.setLength(0.1); //TODO Config
                    break;
                case TableType:
                    building.setColor(config.getACityBuildingColorHex("tableTypeBuilding"));
                    building.setShape(config.getACityBuildingShape("tableTypeBuilding"));
                    building.setWidth(config.getACityBuildingWidth("tableTypeBuilding"));
                    building.setLength(0.3); //TODO Config
            }
        }
    }


    private void designChimney(ACityElement chimney) {
        chimney.setColor(config.getACityChimneyColorHex("attributeColor"));
        chimney.setShape(config.getACityChimneyShape("attributeChimney"));
    }


    private void designFloor(ACityElement floor) {

        Node sourceNode = floor.getSourceNode();

        if (sourceNode != null) {

            String propertyTypeName = sourceNode.get(String.valueOf(SAPNodeProperties.type_name)).asString();

            switch (SAPNodeTypes.valueOf(propertyTypeName)) {
                case Method:
                    floor.setColor(config.getACityFloorColorHex("methodFloor"));
                    floor.setShape(config.getACityFloorShape("methodFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    break;
                case FormRoutine:
                    floor.setColor(config.getACityFloorColorHex("formroutineFloor"));
                    floor.setShape(config.getACityFloorShape("formroutineFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    break;
                case FunctionModule:
                    floor.setColor(config.getACityFloorColorHex("functionModuleFloor"));
                    floor.setShape(config.getACityFloorShape("functionModuleFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    break;
                case TableElement:
                    floor.setColor(config.getACityFloorColorHex("tableElementFloor"));
                    floor.setShape(config.getACityFloorShape("tableElementFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    break;
                case StructureElement:
                    floor.setColor(config.getACityFloorColorHex("structureElementFloor"));
                    floor.setShape(config.getACityFloorShape("structureElementFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    break;
                case DataElement:
                    floor.setColor(config.getACityFloorColorHex("dataElementFloor"));
                    floor.setShape(config.getACityFloorShape("dataElementFloor"));
                    floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
                    floor.setWidth(floor.getWidth() - 0.1); //TODO Config
                    floor.setLength(floor.getLength() - 0.1); //TODO Config
                    break;
            }
        } else
            {
                floor.setColor("#ffffcc"); //TODO Config
                floor.setShape(ACityElement.ACityShape.cone); //TODO Config
                floor.setYPosition(floor.getYPosition() - 0.1); //TODO Config
            }
        }
}
