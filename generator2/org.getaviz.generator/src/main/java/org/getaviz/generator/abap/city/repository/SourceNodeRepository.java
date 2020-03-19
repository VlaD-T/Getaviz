package org.getaviz.generator.abap.city.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;

import java.util.*;

public class SourceNodeRepository {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    /* Node not implements comparable interface to use Sets
    *   -> use Maps with ID to Node */

    private Map<Long, Node> nodeById;

    private Map<String, Map<Long, Node>> nodesByLabel;

    private Map<String,
                Map<Boolean,
                        Map<Long,
                                Map<Long, Node>> > > nodesByRelation;


    public SourceNodeRepository(){
        nodeById = new HashMap<>();
        nodesByLabel = new HashMap<>();
        nodesByRelation = new HashMap<>();
    }

    public void loadNodesWithRelation(SAPRelationLabels relationLabel){

        connector.executeRead(
                " MATCH (m)-[:" + relationLabel.name() + "]->(n) RETURN m, n"
        ).forEachRemaining((result) -> {
            Node mNode = result.get("m").asNode();
            Node nNode = result.get("n").asNode();

            addNodeByID(mNode);
            addNodeByID(nNode);

            /*addNodeByLabel(mNode);
            addNodeByLabel(nNode);*/

            addNodesByProperty(mNode);
            addNodesByProperty(nNode);


            addNodesByRelation(mNode, nNode, relationLabel.name());

        });

    }


    public Collection<Node> getNodes(){
        return nodeById.values();
    }


    public Collection<Node> getRelatedNodes(Node node, SAPRelationLabels relationLabel, Boolean direction){
        if( !nodesByRelation.containsKey(relationLabel.name())){
            return new TreeSet<>();
        };
        Map<Boolean, Map<Long, Map<Long, Node>>> relationMap = nodesByRelation.get(relationLabel.name());

        Map<Long, Map<Long, Node>> directedRelationMap = relationMap.get(direction);

        Long nodeID = node.id();
        if( !directedRelationMap.containsKey(nodeID)){
            return new TreeSet<>();
        }

        return directedRelationMap.get(nodeID).values();
    }

    public Collection<Node> getNodesByLabel(SAPNodeLabels label){
        if( !nodesByLabel.containsKey(label.name())){
            return new TreeSet<>();
        }
        return nodesByLabel.get(label.name()).values();
    }

    //TODO String property durch Enum mit Properties ersetzen
    public Collection<Node> getNodesByProperty(SAPNodeProperties property, String value){
        Collection<Node> nodesByID = getNodes();
        List<Node> nodesByProperty = new ArrayList<>();

        for (Node node: nodesByID) {
            Value propertyValue = node.get(property.toString());
             if( propertyValue == null){
                 continue;
            }
            String propertyValueString = propertyValue.asString();
            if(!propertyValueString.equals(value)){
                continue;
            }

            nodesByProperty.add(node);
        }

        return nodesByProperty;
    }

    // Test Laden Property
    public Collection<Node> getNodesByIdenticalPropertyValuesNodes(SAPNodeProperties property, String value){

        Collection<Node> nodesByLabelAndProperty = new ArrayList<>();

            StatementResult test = connector.executeRead("MATCH (n:Elements {" + property + ": '" + value + "'}) RETURN n");
                test.forEachRemaining((result) -> {
                Node namespace = result.get("n").asNode();

                    nodesByLabelAndProperty.add(namespace);

                });

        return nodesByLabelAndProperty;
    }

    public int getNodesByIdenticalPropertyValuesSize(SAPNodeProperties property, String value){

        Record tests = connector.executeRead("MATCH (n:Elements {" + property + ": '" + value + "'}) RETURN count(n) AS result").next();
        int numberOfVisualizedPackages = tests.get("result").asInt();

        return numberOfVisualizedPackages;
    }
    // Testende Laden Property

    //Test Laden Relationships

    public Collection<Node> getNodesByRelation(SAPRelationLabels relationType){
        return null;
    }

    //Testende Laden Relationships

    public Collection<Node> getNodesByLabelAndProperty(SAPNodeLabels label, String property, String value){
        Collection<Node> nodesByLabel = getNodesByLabel(label);
        List<Node> nodesByLabelAndProperty = new ArrayList<>();

        for (Node node: nodesByLabel){
            Value propertyValue = node.get(property);
            if( propertyValue == null){
                nodesByLabel.remove(node);
            }

            if(propertyValue.asString() != value){
                nodesByLabel.remove(node);
            }

            nodesByLabelAndProperty.add(node);
        }

        return nodesByLabelAndProperty;
    }


    private void addNodeByID(Node node) {
        Long nodeID = node.id();
        if( !nodeById.containsKey(nodeID)){
            nodeById.put(nodeID, node);
        }
    }

    private void addNodesByProperty(Node node) {
        node.labels().forEach( (label)->{
            if( !nodesByLabel.containsKey(label)){
                Map<Long, Node> nodeIDMap = new HashMap<>();
                nodesByLabel.put(label, nodeIDMap);
            }
            Map<Long, Node> nodeIDMap = nodesByLabel.get(label);

            Long nodeID = node.id();
            if( !nodeIDMap.containsValue(nodeID)){
                nodeIDMap.put(nodeID, node);
            }
        });
    }

    private void addNodeByLabel(Node node){
        node.labels().forEach( (label)->{
            if( !nodesByLabel.containsKey(label)){
                Map<Long, Node> nodeIDMap = new HashMap<>();
                nodesByLabel.put(label, nodeIDMap);
            }
            Map<Long, Node> nodeIDMap = nodesByLabel.get(label);

            Long nodeID = node.id();
            if( !nodeIDMap.containsValue(nodeID)){
                nodeIDMap.put(nodeID, node);
            }
        });
    }


   /* private void addNodeByProperty(Node node){
        node.values().forEach( (nodeType)->{
            if( !nodeById.containsKey(nodeType)){
                Map<Long, Node> nodeIDMap = new HashMap<>();
                nodeById.put(nodeType, nodeIDMap);
            }
            Map<Long, Node> nodeIDMap = nodeById.get(nodeType);

            Long nodeID = node.id();
            if( !nodeIDMap.containsValue(nodeID)){
                nodeIDMap.put(nodeID, node);
            }
        });
    }*/

    private void addNodesByRelation(Node mNode, Node nNode, String relationName) {

        if( !nodesByRelation.containsKey(relationName)){
            createRelationMaps(relationName);
        };
        Map<Boolean, Map<Long, Map<Long, Node>>> relationMap = nodesByRelation.get(relationName);

        Map<Long, Map<Long, Node>> forwardRelationMap = relationMap.get(true);
        addNodeToRelationMap(forwardRelationMap, mNode, nNode);

        Map<Long, Map<Long, Node>> backwardRelationMap = relationMap.get(false);
        addNodeToRelationMap(backwardRelationMap, nNode, mNode);
    }

    private void createRelationMaps(String relationName) {
        Map<Boolean, Map<Long, Map<Long, Node>>> relationMap = new HashMap<>();
        Map<Long, Map<Long, Node>> forwardRelationMap = new HashMap<>();
        Map<Long, Map<Long, Node>> backwardRelationMap = new HashMap<>();
        relationMap.put(true, forwardRelationMap);
        relationMap.put(false, backwardRelationMap);
        nodesByRelation.put(relationName, relationMap);
    }

    private void addNodeToRelationMap(Map<Long, Map<Long, Node>> relationMap, Node mNode, Node nNode) {
        Long mNodeID = mNode.id();
        if( !relationMap.containsKey(mNodeID) ){
            Map<Long, Node> nodeIDMap = new HashMap<>();
            relationMap.put(mNodeID, nodeIDMap);
        }
        Map<Long, Node> nodeIDMap = relationMap.get(mNodeID);

        Long nNodeID = nNode.id();
        if( !nodeIDMap.containsKey(nNodeID)){
            nodeIDMap.put(nNodeID, nNode);
        }
    }


}
