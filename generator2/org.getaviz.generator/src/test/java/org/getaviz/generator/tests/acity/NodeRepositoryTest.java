package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.NodeRepository;
import org.getaviz.generator.abap.city.SAPNodeLabels;
import org.getaviz.generator.abap.city.SAPNodeTypes;
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
        mockUp.setupDatabase("./test/databases/CityBankTest.db", "SAPExportCreateNodes.cypher");
        mockUp.runCypherScript("SAPExportCreateContainsRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateTypeOfRelation.cypher");
        mockUp.loadProperties("CityBankTest.properties");

        nodeRepository = new NodeRepository();
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.CONTAINS);
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.TYPEOF);
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    void allNodes(){

        Collection<Node> allNodes = nodeRepository.getNodes();
        assertEquals(340, allNodes.size());

    }

    @Test
    void packageNodes(){
        Collection<Node> packageNodes = nodeRepository.getNodesByProperty("type_name", SAPNodeTypes.Namespace.name());
        assertEquals(17, packageNodes.size());
    }

    @Test
    void domainNodes(){
        Collection<Node> domainNodes = nodeRepository.getNodesByProperty("type_name", SAPNodeTypes.Domain.name());
        assertEquals(27, domainNodes.size());
    }

    @Test
    void containsRelationTest(){
        Collection<Node> packageNodes = nodeRepository.getNodesByProperty("type_name", SAPNodeTypes.Namespace.name());

        Node firstPackage = packageNodes.iterator().next();
        Collection<Node> subNodes = nodeRepository.getRelatedNodes(firstPackage, SAPRelationLabels.CONTAINS, true);
        assertEquals(84, subNodes.size());

        Node subNode = subNodes.iterator().next();
        Collection<Node> parentNodes = nodeRepository.getRelatedNodes(subNode, SAPRelationLabels.CONTAINS, false);
        assertEquals(1, parentNodes.size());
    }

    @Test
    void typeOfRelationTest(){
        Collection<Node> tableTypeNodes = nodeRepository.getNodesByProperty("type_name", SAPNodeTypes.TableType.name());

        Node firstTableType = tableTypeNodes.iterator().next();
        Collection<Node> subNodes = nodeRepository.getRelatedNodes(firstTableType, SAPRelationLabels.TYPEOF,true);
        assertEquals(0, subNodes.size());

        /*Node subNode = subNodes.iterator().next();
        Collection<Node> parentNodes = nodeRepository.getRelatedNodes(subNode, SAPRelationLabels.TYPEOF,false);
        assertEquals(1, parentNodes.size());*/
    }



}
