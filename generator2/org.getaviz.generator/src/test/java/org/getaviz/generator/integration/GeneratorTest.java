package org.getaviz.generator.integration;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.enums.SAPNodeProperties;
import org.getaviz.generator.abap.city.enums.SAPNodeTypes;
import org.getaviz.generator.abap.city.enums.SAPRelationLabels;
import org.getaviz.generator.abap.city.repository.ACityRepository;
import org.getaviz.generator.abap.city.repository.SourceNodeRepository;
import org.getaviz.generator.abap.city.steps.ACityAFrameExporter;
import org.getaviz.generator.abap.city.steps.ACityCreator;
import org.getaviz.generator.abap.city.steps.ACityDesigner;
import org.getaviz.generator.abap.city.steps.ACityLayouter;
import org.getaviz.generator.mockups.ABAPmock;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratorTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ABAPmock mockUp = new ABAPmock();
    private static SourceNodeRepository nodeRepository;
    private static ACityRepository aCityRepository;


    public static void main(String[] args) {

        String cypherScript = "SAPTestExportCreateNodes.cypher";
        String cypherScriptDirectory = "C:\\Getaviz\\generator2\\org.getaviz.generator\\target\\test-classes\\";
        String testFile = "20200214_Test.csv";

        String outputFile = "Test.html";
        String outputDirectory = "C:\\Getaviz\\generator2\\org.getaviz.generator\\test\\output\\";


        String cypherScriptContent = String.format(
            "LOAD CSV WITH HEADERS FROM \"file:///C:/Getaviz/generator2/org.getaviz.generator/src/test/neo4jexport/integration/%s\"\n" +
                    "AS row FIELDTERMINATOR ';'\n" +
                    "CREATE (n:Elements)\n" +
                    "SET n = row", testFile
        );

        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(cypherScriptDirectory + cypherScript));
            buff.write(cypherScriptContent);
            buff.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mockUp.setupDatabase("./test/databases/CityBankTest.db", cypherScript);
        mockUp.runCypherScript("SAPExportCreateContainsRelation.cypher");
        mockUp.runCypherScript("SAPExportCreateUsesRelation.cypher");
        mockUp.loadProperties("ABAPCityTest.properties");

        nodeRepository = new SourceNodeRepository();
        nodeRepository.loadNodesByPropertyValue(SAPNodeProperties.type_name, SAPNodeTypes.Namespace.name());
        nodeRepository.loadNodesByRelation(SAPRelationLabels.CONTAINS, true);

        aCityRepository = new ACityRepository();

        ACityCreator aCityCreator = new ACityCreator(aCityRepository, config);
        aCityCreator.createRepositoryFromNodeRepository(nodeRepository);

        ACityLayouter aCityLayouter = new ACityLayouter(aCityRepository, config);
        aCityLayouter.layoutRepository();

        ACityDesigner designer = new ACityDesigner(aCityRepository, config);
        designer.designRepository();

        ACityAFrameExporter aCityAFrameExporter = new ACityAFrameExporter(aCityRepository, config);

        String htmlFile = aCityAFrameExporter.createAFrameExportFile();

        try {
            BufferedWriter buff = new BufferedWriter(new FileWriter(outputDirectory + outputFile));
            buff.write(htmlFile);
            buff.close();
        } catch (IOException e) {
            System.out.print(htmlFile);
        }

        return;
    }

}
