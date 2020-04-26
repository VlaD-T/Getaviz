package org.getaviz.generator.abap.city.steps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.abap.city.enums.SAPNodeTypes;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.layouts.ACityBuildingLayout;
import org.getaviz.generator.abap.city.layouts.ACityDistrictLayout;
import org.getaviz.generator.abap.city.repository.ACityElement;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.neo4j.driver.v1.types.Node;

import java.util.*;

public class ACityLayouter {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;


    private SourceNodeRepository nodeRepository;
    private ACityRepository repository;

    public ACityLayouter(ACityRepository aCityRepository, SourceNodeRepository sourceNodeRepository, SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
        nodeRepository = sourceNodeRepository;

        log.info("created");
    }


    public void layoutRepository(){

        Collection<ACityElement> buildings = repository.getElementsByType(ACityElement.ACityType.Building);
        for (ACityElement building: buildings) {

            SAPNodeTypes sourceNodeType = building.getSourceNodeType();

            if(sourceNodeType == SAPNodeTypes.TableType) {
                SAPNodeTypes buildingSourceType = getTableTypeTypeOfType(building);
                if(buildingSourceType != null){
                    layoutTableTypeBuilding(building, buildingSourceType);
                }
            } else {
                layoutBuilding(building);
            }
        }

        layoutParentDistricts(buildings);
    }

    private SAPNodeTypes getTableTypeTypeOfType(ACityElement building) {

        Node tableTypeSourceNode = building.getSourceNode();

        Collection<Node> typeOfNodes = nodeRepository.getRelatedNodes(tableTypeSourceNode, SAPRelationLabels.TYPEOF, true);
        if(typeOfNodes.isEmpty()){
            String tableTypeName = tableTypeSourceNode.get(SAPNodeProperties.object_name.name()).asString();
            log.warn("TYPEOF related nodes not found for tableType \"" + tableTypeName + "\"");
            return null;
        }
        if(typeOfNodes.size() != 1){
            String tableTypeName = tableTypeSourceNode.get(SAPNodeProperties.object_name.name()).asString();
            log.error("TYPEOF related nodes more than 1 for tableType \"" + tableTypeName + "\"");
            return null;
        }

        Node typeOfNode = typeOfNodes.iterator().next();

        String typeOfNodeTypeProperty = typeOfNode.get(SAPNodeProperties.type_name.name()).asString();

        return SAPNodeTypes.valueOf(typeOfNodeTypeProperty);
    }

    private void layoutTableTypeBuilding(ACityElement building, SAPNodeTypes typeOfType) {

        switch (typeOfType){
            case Class:
            case Interface:
                building.setHeight(5); //TODO Config
                break;
            case Table:
            case TableType:
                building.setHeight(4); //TODO Config
                break;
            case Structure:
                building.setHeight(2); //TODO Config
                break;
            case DataElement:
                building.setHeight(1); //TODO Config
            default:
                Node sourceNode = building.getSourceNode();
                String tableTypeName = sourceNode.get(SAPNodeProperties.object_name.name()).asString();
                log.error("Type \"" + typeOfType + "\" not allowed for tableType-Element \"" +  tableTypeName );
        }


        Double groundAreaLength = config.getACityGroundAreaByChimneyAmount();
        building.setWidth(groundAreaLength);
        building.setLength(groundAreaLength);

        building.setXPosition(0.0);
        building.setYPosition(building.getHeight() / 2);
        building.setZPosition(0.0);
    }

    private void layoutParentDistricts(Collection<ACityElement> districtElements) {

        Collection<ACityElement> parentDistricts = getParentDistricts(districtElements);

        if (parentDistricts.isEmpty()){
            layoutVirtualRootDistrict(districtElements);
            return;
        }

        for(ACityElement parentDistrict : parentDistricts){
            layoutDistrict(parentDistrict);
        }

        layoutParentDistricts(parentDistricts);
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

    private void layoutDistrict(ACityElement district) {
        Collection<ACityElement> subElements = district.getSubElements();

        ACityDistrictLayout aCityDistrictLayout = new ACityDistrictLayout(district,  subElements, config);
        aCityDistrictLayout.calculate();
    }

    private void layoutVirtualRootDistrict(Collection<ACityElement> districts){
        ACityElement virtualRootDistrict = new ACityElement(ACityElement.ACityType.District);

        ACityDistrictLayout aCityDistrictLayout = new ACityDistrictLayout(virtualRootDistrict,  districts, config);
        aCityDistrictLayout.calculate();
    }

}
