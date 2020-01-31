package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.*;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CreatorTest {

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
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    void districtElements() {
        Collection<ACityElement> districts = aCityRepository.getElementsByType(ACityElement.ACityType.District);
        assertEquals(7, districts.size());
    }

    @Test
    void buildingElements() {
        Collection<ACityElement> buildings = aCityRepository.getElementsByType(ACityElement.ACityType.Building);
        assertEquals(8, buildings.size());
    }

    @Test
    void buildingParentElements() {
        Collection<ACityElement> buildings = aCityRepository.getElementsByType(ACityElement.ACityType.Building);

        for (ACityElement building : buildings) {
            assertNotEquals(null, building.getParentElement());

            ACityElement.ACityType parentType = building.getParentElement().getType();
            assertEquals(ACityElement.ACityType.District, parentType);
        }
    }

    @Test
    void buildingSubElements(){
        Collection<ACityElement> buildings = aCityRepository.getElementsByTypeAndSourceProperty(ACityElement.ACityType.Building, "object_name", "/GSA/VISAP_T_TEST_CLASS");

        ACityElement firstBuilding = buildings.iterator().next();
        assertNotEquals(0, firstBuilding.getSubElements().size());
    }

    @Test
    void floorParentElements(){
        Collection<ACityElement> floors = aCityRepository.getElementsByType(ACityElement.ACityType.Floor);

        for (ACityElement floor : floors) {
            assertNotEquals(null, floor.getParentElement());

            ACityElement.ACityType parentType = floor.getParentElement().getType();
            assertEquals(ACityElement.ACityType.Building, parentType);
        }
    }

    @Test
    void chimneyParentElements(){
        Collection<ACityElement> chimneys = aCityRepository.getElementsByType(ACityElement.ACityType.Chimney);

        for (ACityElement chimney : chimneys) {
            assertNotEquals(null, chimney.getParentElement());

            ACityElement.ACityType parentType = chimney.getParentElement().getType();
            assertEquals(ACityElement.ACityType.Building, parentType);
        }
    }


    @Test
    void districtSubElements(){

        //first district
        Collection<ACityElement> districts = aCityRepository.getElementsByTypeAndSourceProperty(ACityElement.ACityType.District, "object_name", "/GSA/VISAP_T_TEST");
        assertEquals(1, districts.size());

        ACityElement firstDistrict = districts.iterator().next();

        Collection<ACityElement> subDistricts = firstDistrict.getSubElements();
        assertEquals(1, subDistricts.size());


        //second district
        districts = aCityRepository.getElementsByTypeAndSourceProperty(ACityElement.ACityType.District, "object_name", "/GSA/VISAP_T");
        assertEquals(1, districts.size());

        ACityElement secondDistrict = districts.iterator().next();

        subDistricts = secondDistrict.getSubElements();
        assertEquals(4, subDistricts.size());
    }


}
