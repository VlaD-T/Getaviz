package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.*;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        nodeRepository.loadNodesWithRelation( SAPRelationLabels.DECLARES);

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
        assertEquals(2, districts.size());
    }

    @Test
    void buildingElements() {
        Collection<ACityElement> buildings = aCityRepository.getElementsByType(ACityElement.ACityType.Building);
        assertEquals(4, buildings.size());
    }
}
