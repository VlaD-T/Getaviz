package org.getaviz.generator.tests.abapMetropolis;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.abap.city.enums.SAPNodeTypes;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.getaviz.generator.abap.city.steps.*;
import org.getaviz.generator.mockups.ABAPmock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Metro_AFrameExporterTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static SourceNodeRepository nodeRepository;
    private static ACityRepository aCityRepository;

    private static String exportString;

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

        aCityRepository = new ACityRepository();

        MetropolisCreator metropolisCreator = new MetropolisCreator(aCityRepository, nodeRepository, config);
        metropolisCreator.createRepositoryFromNodeRepository();

        MetropolisLayouter metropolisLayouter = new MetropolisLayouter(aCityRepository, nodeRepository, config);
        metropolisLayouter.layoutRepository();

        MetropolisDesigner metropolisDesigner = new MetropolisDesigner(aCityRepository, nodeRepository, config);
        metropolisDesigner.designRepository();

        ACityAFrameExporter aCityAFrameExporter = new ACityAFrameExporter(aCityRepository, config);
        exportString = aCityAFrameExporter.createAFrameExportFile();
    }

    @AfterAll
    static void close() {
        mockUp.close();
    }

    @Test
    public void export(){
        assertNotEquals("", exportString);

        System.out.println(exportString);
    }

}
