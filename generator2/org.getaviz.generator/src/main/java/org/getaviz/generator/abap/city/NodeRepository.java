package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.driver.v1.types.Node;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;

import java.util.*;

public class NodeRepository {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    /* Node not implements comparable interface
    *   -> use Maps with ID to Nodes */

    private Map<Long, Node> nodeById;

    private Map<String, Map<Long, Node>> nodesByLabel;

    private Map<String,
                Map<Boolean,
                        Map<Long,
                                Map<Long, Node>> > > nodesByRelation;


    public NodeRepository(){
        nodeById = new HashMap<>();
        nodesByLabel = new HashMap<>();
        nodesByRelation = new HashMap<>();
    }

    public void loadNodesWithRelation(String relationName){

        connector.executeRead(
                " MATCH (m)-[:" + relationName + "]->(n) RETURN m, n"
        ).forEachRemaining((result) -> {
            Node mNode = result.get("m").asNode();
            Node nNode = result.get("n").asNode();

            addNodeByID(mNode);
            addNodeByID(nNode);

            addNodeByLabel(mNode);
            addNodeByLabel(nNode);

            addNodesByRelation(mNode, nNode, relationName);

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

    public Collection<Node> getRelatedNodes(Node node, String relationName, Boolean direction){
        if( !nodesByRelation.containsKey(relationName)){
            return new TreeSet<>();
        };
        Map<Boolean, Map<Long, Map<Long, Node>>> relationMap = nodesByRelation.get(relationName);

        Map<Long, Map<Long, Node>> directedRelationMap = relationMap.get(direction);

        Long nodeID = node.id();
        if( !directedRelationMap.containsKey(nodeID)){
            return new TreeSet<>();
        }

        return directedRelationMap.get(nodeID).values();
    }

    public Collection<Node> getNodesByLabel(String label){
        if( !nodesByLabel.containsKey(label)){
            return new TreeSet<>();
        }
        return nodesByLabel.get(label).values();
    }

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

    private void addNodeByID(Node node) {
        Long nodeID = node.id();
        if( !nodeById.containsKey(nodeID)){
            nodeById.put(nodeID, node);
        }
    }

}
