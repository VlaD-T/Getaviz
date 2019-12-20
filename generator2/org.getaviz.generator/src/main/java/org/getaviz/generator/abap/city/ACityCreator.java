package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.driver.v1.types.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ACityCreator {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    private ACityRepository repository;

    public ACityCreator(ACityRepository aCityRepository,SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
    }



    public void createRepositoryFromNodeRepository(NodeRepository nodeRepository){

        createACityElementsFromSourceNodes(repository, nodeRepository, "Package", ACityElement.ACityType.District);

        createACityElementsFromSourceNodes(repository, nodeRepository, "Report", ACityElement.ACityType.Building);

        createACityElementsFromSourceNodes(repository, nodeRepository, "Class", ACityElement.ACityType.Building);

        createACityElementsFromSourceNodes(repository, nodeRepository, "FunctionGroup", ACityElement.ACityType.Building);

        createACityElementsFromSourceNodes(repository, nodeRepository, "Table", ACityElement.ACityType.Building);



    }

    private void createACityElementsFromSourceNodes(ACityRepository repository, NodeRepository nodeRepository, String nodeLabel, ACityElement.ACityType aCityType) {
        Collection<Node> sourceNodes = nodeRepository.getNodesByLabel(nodeLabel);
        List<ACityElement> aCityElements = createACityElements(sourceNodes, aCityType);
        repository.addElements(aCityElements);
    }

    private List<ACityElement> createACityElements(Collection<Node> sourceNodes, ACityElement.ACityType aCityType) {
        List<ACityElement> aCityElements = new ArrayList<>();

        for( Node sourceNode: sourceNodes ) {
            ACityElement aCityElement = new ACityElement(aCityType);
            aCityElement.setSourceNodeID(sourceNode.id());
            aCityElements.add(aCityElement);
        }

        return aCityElements;
    }

}
