package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.abap.city.enums.SAPNodeTypes;
import org.getaviz.generator.abap.city.repository.ACityElement;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.enums.SAPNodeLabels;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.getaviz.generator.abap.city.steps.ACityCreator;
import org.getaviz.generator.abap.city.steps.ACityDesigner;
import org.getaviz.generator.abap.city.steps.ACityLayouter;
import org.getaviz.generator.database.DatabaseConnector;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class WriteDataToNeo4jTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static SourceNodeRepository nodeRepository;
    private static ACityRepository aCityRepository;

    static DatabaseConnector connector;

    @BeforeAll
    static void setup() {
        mockUp.setupDatabase("./test/databases/CityBankTest.db", "SAPExportCreateNodes.cypher");

        mockUp.runCypherScript("SAPExportCreateContainsRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateTypeOfRelation.cypher");

        mockUp.loadProperties("ABAPCityTest.properties");

        nodeRepository = new SourceNodeRepository();
        nodeRepository.loadNodesByPropertyValue(SAPNodeProperties.type_name, SAPNodeTypes.Namespace.name());
        nodeRepository.loadNodesByRelation(SAPRelationLabels.CONTAINS, true);
        nodeRepository.loadNodesByRelation(SAPRelationLabels.TYPEOF, true);

        ACityCreator aCityCreator = new ACityCreator(aCityRepository, nodeRepository, config);
        aCityCreator.createRepositoryFromNodeRepository();

        ACityDesigner designer = new ACityDesigner(aCityRepository, nodeRepository, config);
        designer.designRepository();

        ACityLayouter aCityLayouter = new ACityLayouter(aCityRepository, nodeRepository, config);
        aCityLayouter.layoutRepository();

        aCityRepository.writeRepositoryToNeo4j();
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    void checkIfElementsAreAddedToNeo4jD() {

        Record districtResult = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.District + "' }) RETURN count(n) AS result")
                .single();
        int numberOfVisualizedPackages = districtResult.get("result").asInt();
        assertEquals(42, numberOfVisualizedPackages);
    }

    @Test
    void checkNotAddedElementsToNeo4j() {

        Record results = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.Building + "' }) RETURN count(n) AS result")
                .single();
        int numberOfVisualized = results.get("result").asInt();
        assertEquals(118, numberOfVisualized);
    }


    @Test
    void checkIfElementsAreAddedToNeo4j() {

        Record result = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.Chimney + "' }) RETURN count(n) AS result")
                .single();
        int numberOfVisualizedPackages = result.get("result").asInt();
        assertEquals(66, numberOfVisualizedPackages);
    }



    @Test
    void checkIfElementsAreAddedToNeo4jF() {

        Record floorResult = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.Floor + "' }) RETURN count(n) AS result")
                .single();
        int numberOfVisualizedPackages = floorResult.get("result").asInt();
        assertEquals(100, numberOfVisualizedPackages);
    }





    @Test
    void checkPropertiesFromAddedElementsToNeo4j() {

        Record colorResult = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.Chimney + "' }) RETURN n.color AS result").next();
        String color = colorResult.get("result").asString();
        assertEquals("#FFFF00", color);
    }


    @Test
    void checkBuildingLayout(){

        Record heightResult = connector
                .executeRead("MATCH (n:Elements {cityType : '" + ACityElement.ACityType.Chimney + "' }) RETURN n.height AS result").next();
        double height = heightResult.get("result").asDouble();
        assertEquals(0.5, height);
    }

    @Test
    void loadedNodesNew(){

        Record allNodesNew = connector.executeRead("MATCH (n) RETURN count(n) AS result").single();
        int numberOfAllNodes = allNodesNew.get("result").asInt();
        assertEquals(666, numberOfAllNodes); //340 Elelemente vorher + 100Floors + 66 chimneys + 42 Distrikte + 118 Buildings = 326 -
    }

    @Test
    void NodesWithContainsRelation() {

        Record containNodes = connector.executeRead("MATCH p=()-[r:CONTAINS]->() RETURN count(p) AS result").single();
        int numberOfContainsNodes = containNodes.get("result").asInt();
        assertEquals(296, numberOfContainsNodes); //40 uses -  4 fehlende contains -> 340 - 44 = 296!
    }

    @Test
    void NodesWithSourceRelation() {

        Record sourceResult = (Record) connector.executeRead("MATCH p=()<-[r:SOURCE]-() RETURN count(p) AS result").single();
        int sourceResults = sourceResult.get("result").asInt();

        assertEquals(301, sourceResults); // 326 (contains) - typeDistricts (25) = 301
    }

    @Test
    void NodesWithChildRelation() {

        Record sourceResult = (Record) connector.executeRead("MATCH p=()-[r:CHILD]->() RETURN count(p) AS result").single();
        int sourceResults = sourceResult.get("result").asInt();

        //TODO Amount not reasonable
        assertEquals(308, sourceResults); // 326 (contains) + 25 typedistricts = 351
    }

}
