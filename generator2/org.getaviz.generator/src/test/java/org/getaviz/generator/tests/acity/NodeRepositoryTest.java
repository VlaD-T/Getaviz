package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.abap.city.enums.SAPNodeTypes;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.repository.ACityElement;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.getaviz.generator.database.DatabaseConnector;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeRepositoryTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static SourceNodeRepository nodeRepository;

    static DatabaseConnector connector;

    @BeforeAll
    static void setup() {
        mockUp.setupDatabase("./test/databases/CityBankTest.db", "SAPExportCreateNodes.cypher");
        mockUp.runCypherScript("SAPExportCreateContainsRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateUsesRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateTypeOfRelation.cypher");
        mockUp.loadProperties("CityBankTest.properties");
        connector = mockUp.getConnector();

        nodeRepository = new SourceNodeRepository();
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.CONTAINS);
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.TYPEOF);
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.USES);
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    void NodesByRelation(){
        Collection<Node> nodesByRelationTest = nodeRepository.getNodesByRelation(SAPRelationLabels.USES);
        //  assertEquals(17, nodesByRelationTest.size());
    }

    @Test
    void NodesByRelatin(){
         nodeRepository.loadNodesByPropertyValue(SAPNodeProperties.type_name, SAPNodeTypes.DataElement.name());

         // Test if method works correctly
        Record PropertyValueResults = connector
                .executeRead("MATCH (n:Elements { " + SAPNodeProperties.type_name + " : '" + SAPNodeTypes.DataElement.name() + "' }) RETURN count(n) AS result")
                .single();
        int numberOfVisualized = PropertyValueResults.get("result").asInt();
        assertEquals(18, numberOfVisualized);
    }

    @Test
    void NodesByIdenticalPropertyValue(){
        int packageNodes = nodeRepository.getNodesByIdenticalPropertyValuesSize(SAPNodeProperties.type_name, SAPNodeTypes.Class.name());
        assertEquals(21, packageNodes);
    }

    @Test
    void NodesByIdenticalPropertyValueNode(){
        Collection<Node> packageNodes = nodeRepository.getNodesByIdenticalPropertyValuesNodes(SAPNodeProperties.type_name, SAPNodeTypes.Class.name());
        assertEquals(21, packageNodes.size());
    }

}
