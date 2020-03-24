package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.getaviz.generator.abap.city.steps.ACityAFrameExporter;
import org.getaviz.generator.abap.city.steps.ACityCreator;
import org.getaviz.generator.abap.city.steps.ACityDesigner;
import org.getaviz.generator.abap.city.steps.ACityLayouter;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.types.Node;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IntegrationTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static SourceNodeRepository nodeRepository;
    private static ACityRepository aCityRepository;

    private static String exportString;

    @BeforeAll
    static void setup() {

        mockUp.setupDatabase("./test/databases/CityBankTest.db", "SAPExportCreateNodes.cypher");
        mockUp.runCypherScript("SAPExportCreateContainsRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateUsesRelation.cypher");
        mockUp.loadProperties("CityBankTest.properties");

        nodeRepository = new SourceNodeRepository();
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.CONTAINS);
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.TYPEOF);
        nodeRepository.loadNodesWithRelation(SAPRelationLabels.USES);

        aCityRepository = new ACityRepository();

        ACityCreator aCityCreator = new ACityCreator(aCityRepository, config);
        aCityCreator.createRepositoryFromNodeRepository(nodeRepository);

        ACityLayouter aCityLayouter = new ACityLayouter(aCityRepository, config);
        aCityLayouter.layoutRepository();

        ACityDesigner designer = new ACityDesigner(aCityRepository, config);
        designer.designRepository();

        ACityAFrameExporter aCityAFrameExporter = new ACityAFrameExporter(aCityRepository, config);
        exportString = aCityAFrameExporter.createAFrameExportFile();
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    public void nodesLoaded(){
        Collection<Node> allNodes = nodeRepository.getNodes();
        assertEquals(340, allNodes.size());
    }


    @Test
    public void export(){
        assertNotEquals("", exportString);

        System.out.println(exportString);
    }


}
