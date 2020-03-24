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

public class NodeRepositoryPropertyRelationLoadTest {

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

        nodeRepository.loadNodesByPropertyValue(SAPNodeProperties.type_name, SAPNodeTypes.DataElement.name());
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
    void NodesByProperty(){
        int nodeAmount = nodeRepository.getNodes().size();

        assertEquals(19, nodeAmount);
    }




}
