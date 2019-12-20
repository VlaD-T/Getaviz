package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.NodeRepository;
import org.getaviz.generator.abap.city.SAPNodeLabels;
import org.getaviz.generator.abap.city.SAPRelationLabels;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.types.Node;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeRepositoryTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static NodeRepository nodeRepository;

    @BeforeAll
    static void setup() {
        mockUp.setupDatabase("./test/databases/CityBankTest.db");
        mockUp.loadProperties("CityBankTest.properties");

        nodeRepository = new NodeRepository();
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.CONTAINS);
        nodeRepository.loadNodesWithRelation( SAPRelationLabels.DECLARES);
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    void allNodes(){

        Collection<Node> allNodes = nodeRepository.getNodes();
        assertEquals(10, allNodes.size());

    }

    @Test
    void packageLabel(){
        Collection<Node> packageNodes = nodeRepository.getNodesByLabel(SAPNodeLabels.Package);
        assertEquals(2, packageNodes.size());
    }

    @Test
    void typeLabel(){
        Collection<Node> typeNodes = nodeRepository.getNodesByLabel(SAPNodeLabels.Type);
        assertEquals(4, typeNodes.size());
    }

    @Test
    void containsRelationTest(){
        Collection<Node> packageNodes = nodeRepository.getNodesByLabel(SAPNodeLabels.Package);

        Node firstPackage = packageNodes.iterator().next();
        Collection<Node> subNodes = nodeRepository.getRelatedNodes(firstPackage, SAPRelationLabels.CONTAINS, true);
        assertEquals(3, subNodes.size());

        Node subNode = subNodes.iterator().next();
        Collection<Node> parentNodes = nodeRepository.getRelatedNodes(subNode, SAPRelationLabels.CONTAINS, false);
        assertEquals(1, parentNodes.size());
    }

    @Test
    void declaresRelationTest(){
        Collection<Node> reportNodes = nodeRepository.getNodesByLabel(SAPNodeLabels.Report);

        Node firstReport = reportNodes.iterator().next();
        Collection<Node> subNodes = nodeRepository.getRelatedNodes(firstReport, SAPRelationLabels.DECLARES, true);
        assertEquals(1, subNodes.size());

        Node subNode = subNodes.iterator().next();
        Collection<Node> parentNodes = nodeRepository.getRelatedNodes(subNode, SAPRelationLabels.DECLARES, false);
        assertEquals(1, parentNodes.size());
    }



}
