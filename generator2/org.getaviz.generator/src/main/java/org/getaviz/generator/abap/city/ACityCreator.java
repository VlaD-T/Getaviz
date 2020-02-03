package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.neo4j.driver.v1.types.Node;

import java.util.*;

public class ACityCreator {

    //TODO
    // Aufteilung TypeDistricte nicht optimal
    // Integration TableTypeElements nicht optimal

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityRepository repository;

    public ACityCreator(ACityRepository aCityRepository,SettingsConfiguration config) {
        this.config = config;

        repository = aCityRepository;
    }



    public void createRepositoryFromNodeRepository(NodeRepository nodeRepository){

        createACityElementsFromTableTypeTypeOfRelation(nodeRepository);

        createAllACityElements(nodeRepository);

        createAllACityRelations(nodeRepository);

    }

    private void createAllACityElements(NodeRepository nodeRepository) {
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Package, ACityElement.ACityType.District);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Report, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.FormRoutine, ACityElement.ACityType.Floor);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Class, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Interface, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Method, ACityElement.ACityType.Floor);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Attribute, ACityElement.ACityType.Chimney);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.FunctionGroup, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.FunctionModule, ACityElement.ACityType.Floor);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Table, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.TableElement, ACityElement.ACityType.Floor);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Structure, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.StructureElement, ACityElement.ACityType.Floor);

        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.DataElement, ACityElement.ACityType.Building);
        createACityElementsFromSourceNodes(nodeRepository, SAPNodeLabels.Domain, ACityElement.ACityType.Building);
    }




    private void createTypeDistricts(ACityElement parentDistrict, Collection<ACityElement> childElements) {
        Map<ACityElement.ACitySubType, ACityElement> typeDistrictMap = new HashMap<>();

        for (ACityElement childElement: childElements) {
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.Class, SAPNodeLabels.Class);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.Class, SAPNodeLabels.Interface);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.Report, SAPNodeLabels.Report);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.FunctionGroup,SAPNodeLabels.FunctionGroup);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.Table, SAPNodeLabels.Table);

            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.DDIC, SAPNodeLabels.Structure);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.DDIC, SAPNodeLabels.Domain);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.DDIC, SAPNodeLabels.DataElement);
            addChildToTypeDistrict(parentDistrict, childElement, typeDistrictMap, ACityElement.ACitySubType.DDIC, SAPNodeLabels.TableType);
        }
    }


    private void createACityElementsFromTableTypeTypeOfRelation(NodeRepository nodeRepository) {
        Collection<Node> tableTypeSourceNodes = nodeRepository.getNodesByLabel(SAPNodeLabels.TableType);

        List<ACityElement> tableTypeElements = createACityElements(tableTypeSourceNodes, ACityElement.ACityType.Building);
        repository.addElements(tableTypeElements);

        for(ACityElement tableTypeElement : tableTypeElements){

            Node tableTypeSourceNode = tableTypeElement.getSourceNode();

            Collection<Node> typeOfNodes = nodeRepository.getRelatedNodes(tableTypeSourceNode,SAPRelationLabels.TYPEOF, true);
            if(typeOfNodes.size() != 1){
                //TODO Exception
                continue;
            }
            Node typeOfNode = typeOfNodes.iterator().next();



            List<ACityElement> tableTypeSubElements;

            Collection<Node> subNodes = nodeRepository.getRelatedNodes(typeOfNode,SAPRelationLabels.CONTAINS, true);
            if( subNodes.isEmpty() ){
                tableTypeSubElements = createACityElements(typeOfNodes, ACityElement.ACityType.Floor);
            } else {
                tableTypeSubElements = createACityElements(subNodes, ACityElement.ACityType.Floor);
            }

            repository.addElements(tableTypeSubElements);

            for(ACityElement tableTypeSubElement : tableTypeSubElements){
                tableTypeSubElement.setParentElement(tableTypeElement);
                tableTypeElement.addSubElement(tableTypeSubElement);
            }

        }
    }


    private void createAllACityRelations(NodeRepository nodeRepository) {

        Collection<ACityElement> aCityElements = repository.getAllElements();

        for (ACityElement element: aCityElements){

            Node sourceNode = element.getSourceNode();
            Collection<ACityElement> childElements = getChildElementsBySourceNode(nodeRepository, sourceNode);

            if( element.getType() == ACityElement.ACityType.District){

                createTypeDistricts(element, childElements);

            } else {
                for (ACityElement childElement: childElements) {
                    element.addSubElement(childElement);
                    childElement.setParentElement(element);
                }
            }
        }

    }



    private void addChildToTypeDistrict(ACityElement parentDistrict, ACityElement childElement, Map<ACityElement.ACitySubType, ACityElement> typeDistrictMap, ACityElement.ACitySubType districtType, SAPNodeLabels sapNodeLabel) {

        Node childSourceNode = childElement.getSourceNode();

        if( childSourceNode.hasLabel( sapNodeLabel.name()) ){
            if( !typeDistrictMap.containsKey(districtType)){
                ACityElement typeDistrict = new ACityElement(ACityElement.ACityType.District);
                typeDistrict.setSubType(districtType);
                typeDistrictMap.put(districtType, typeDistrict);

                repository.addElement(typeDistrict);
                parentDistrict.addSubElement(typeDistrict);
                typeDistrict.setParentElement(parentDistrict);
            }

            ACityElement typeDistrict = typeDistrictMap.get(districtType);

            typeDistrict.addSubElement(childElement);
            childElement.setParentElement(typeDistrict);
        }

    }

    private Collection<ACityElement> getChildElementsBySourceNode(NodeRepository nodeRepository, Node node) {
        Collection<Node> childNodes = nodeRepository.getRelatedNodes(node, SAPRelationLabels.CONTAINS, true);
        if( childNodes.isEmpty()){
            return new TreeSet<>();
        }

        List<ACityElement> childElements = new ArrayList<>();
        for (Node childNode: childNodes ) {
            Long childNodeID = childNode.id();
            ACityElement childElement = repository.getElementBySourceID(childNodeID);
            childElements.add(childElement);
        }
        return childElements;
    }

    private ACityElement getParentElementBySourceNode(NodeRepository nodeRepository, Node node) {
        Collection<Node> parentNodes = nodeRepository.getRelatedNodes(node, SAPRelationLabels.CONTAINS, false);
        if(parentNodes.isEmpty()) {
            return null;
        }

        Node parentNode = parentNodes.iterator().next();
        Long parentNodeId = parentNode.id();

        ACityElement parentElement = repository.getElementBySourceID(parentNodeId);
        return parentElement;
    }




    private void createACityElementsFromSourceNodes(NodeRepository nodeRepository, SAPNodeLabels nodeLabel, ACityElement.ACityType aCityType) {
        Collection<Node> sourceNodes = nodeRepository.getNodesByLabel(nodeLabel);
        List<ACityElement> aCityElements = createACityElements(sourceNodes, aCityType);
        repository.addElements(aCityElements);
    }

    private List<ACityElement> createACityElements(Collection<Node> sourceNodes, ACityElement.ACityType aCityType) {
        List<ACityElement> aCityElements = new ArrayList<>();

        for( Node sourceNode: sourceNodes ) {
            ACityElement aCityElement = new ACityElement(aCityType);
            aCityElement.setSourceNode(sourceNode);
            aCityElements.add(aCityElement);
        }

        return aCityElements;
    }

}
