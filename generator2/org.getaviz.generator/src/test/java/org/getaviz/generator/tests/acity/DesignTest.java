package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.*;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DesignTest{

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static NodeRepository nodeRepository;
    private static ACityRepository aCityRepository;

    @BeforeAll
    static void setup() {

        mockUp.setupDatabase("./test/databases/CityBankTest.db");
        mockUp.loadProperties("CityBankTest.properties");

        nodeRepository = new NodeRepository();
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.CONTAINS);

        aCityRepository = new ACityRepository();

        ACityCreator aCityCreator = new ACityCreator(aCityRepository, config);
        aCityCreator.createRepositoryFromNodeRepository(nodeRepository);

        ACityDesigner designer = new ACityDesigner(aCityRepository, config);
        designer.designRepository();
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    public void districtDesign(){
        Collection<ACityElement> districts = aCityRepository.getElementsByType(ACityElement.ACityType.District);
        for( ACityElement district : districts){
             assertEquals("#FE2EF7", district.getColor());
             assertEquals(ACityElement.ACityShape.box, district.getShape());
        }
    }

    @Test
    public void buildingDesign(){
        Collection<ACityElement> buildings = aCityRepository.getElementsByType(ACityElement.ACityType.Building);
        for( ACityElement building : buildings){
            assertEquals("#2ECCFA", building.getColor());
            assertEquals(ACityElement.ACityShape.box, building.getShape());
        }
    }

    @Test
    public void floorDesign(){
        Collection<ACityElement> floors = aCityRepository.getElementsByType(ACityElement.ACityType.Floor);
        for( ACityElement floor : floors){
            assertEquals("#013ADF", floor.getColor());
            assertEquals(ACityElement.ACityShape.box, floor.getShape());
        }
    }

    @Test
    public void chimneyDesign(){
        Collection<ACityElement> chimneys = aCityRepository.getElementsByType(ACityElement.ACityType.Chimney);
        for( ACityElement chimney : chimneys){
            assertEquals("#FFFF00", chimney.getColor());
            assertEquals(ACityElement.ACityShape.cylinder, chimney.getShape());
        }
    }


}
