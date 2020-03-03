package org.getaviz.generator.abap.city.m2m;

//import org.getaviz.generator.ColorGradient;
import org.getaviz.generator.SettingsConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;

/*public class ACity2ACity {
	private SettingsConfiguration config = SettingsConfiguration.getInstance();
	private Log log = LogFactory.getLog(this.getClass());
	private HashMap<Long, HashMap<String, String>> nodeProperties = new HashMap<Long, HashMap<String, String>>(); //TODO kill member attribute
	private Node model;
	private DatabaseConnector connector = DatabaseConnector.getInstance();
 
	public ACity2ACity() {		
		log.info("ACity2ACity started");
		
		
		model = connector.executeRead(
				"MATCH (n:Model {building_type: \'" + config.getBuildingTypeAsString() +
				"\'}) RETURN n").next().get("n").asNode();
		System.out.println("Model :" + model);
				
		StatementResult districtResult = connector.executeRead("MATCH (d:District) RETURN d"); //Pakete
		
		List<Node> districts = getNodesOfResult(districtResult, "d"); 
		System.out.println("District :" + districts);
		
		districts.forEach((district) -> {
			//TODO Unterscheidung SC, DDIC, Table
			processDistrict(district);
		});		
		
		
		//TODO LayoutDistricts
//		CityLayout.cityLayout(model.id(), properties);
		//TODO Save all Districts -> Buildings -> BParts
		
		
		log.info("ACity2ACity finished");
	}	
	
	
	//Districts
	
	private void processDistrict(Node district) {
		
		StatementResult buildingResult = connector.executeRead("MATCH (b:Building) RETURN b");
		List<Node> buildings = getNodesOfResult(buildingResult, "b");
		System.out.println("Buildings:" + buildings);
		
		buildings.forEach((building) -> {
			processBuilding(building);
		});		
		
		//layout
		
		buildings.forEach((building) -> {		
			saveBuildingProperties(building);
		});	
		
//		HashMap<String, String> districtProperties = getDistrictProperties(district, buildings);
//		nodeProperties.put(district.id(), districtProperties);
	}
	
	
	
	//Buildings
	
	private void processBuilding(Node building) {
		
		StatementResult buildingPartsResult = connector.executeRead("MATCH (p:BuildingPart) RETURN p"); //TODO Where 
		List<Node> buildingParts = getNodesOfResult(buildingPartsResult, "p");
		System.out.println("BuildingParts:" + buildingParts);
		
		//Parts
		buildingParts.forEach((buildingPart) -> {		
			processBuildingPart(buildingPart);
		});				
		
		layoutBuildingParts(buildingParts);
		
		buildingParts.forEach((buildingPart) -> {		
			saveBuildingPartProperties(buildingPart);
		});
		
		//Building
		HashMap<String, String> buildingProperties = getBuildingProperties(building, buildingParts);
		nodeProperties.put(building.id(), buildingProperties);	
	}
	
	private HashMap<String, String> getBuildingProperties(Node building, List<Node> buildingParts){
		HashMap<String, String> buildingProperties = new HashMap<String, String>();

		Node sourceNode = getSourceNode(building.id());
		
		String color = config.getACityColorHex(sourceNode.get("type").asString()); 
		buildingProperties.put("color", color);
		
		
		double height = config.getHeightMin(); //ACity spezifisch
		buildingProperties.put("height", String.valueOf(height));
		

		double width;
		double length;
		
		
		return buildingProperties;
	}
	
	private void saveBuildingProperties(Node building) {
		
		HashMap<String, String> buildingPartProperties = nodeProperties.get(building.id());
		
		String height = buildingPartProperties.get("height");
		String color = buildingPartProperties.get("color");
		
		connector.executeWrite(
		        String.format("MATCH (n) WHERE ID(n) = %d SET n.height =\'%s\', n.color = \'%s\'", building.id(),
		        		height, color));
	}
	
	
	
	
	//BuildingParts
	
	private void processBuildingPart(Node buildingPart) {
		HashMap<String, String> buildingPartProperties = getBuildingPartProperties(buildingPart);
		nodeProperties.put(buildingPart.id(), buildingPartProperties);
	};
	
	
	
	private HashMap<String, String> getBuildingPartProperties(Node buildingPart){
		HashMap<String, String> buildingPartProperties = new HashMap<String, String>();
		
		Node sourceNode = getSourceNode(buildingPart.id());
		
		String color = config.getACityColorHex(sourceNode.get("type").asString()); 
		buildingPartProperties.put("color", color);		
		
		double height = config.getHeightMin();
		buildingPartProperties.put("height", String.valueOf(height));
		

		double width = config.getWidthMin();
		buildingPartProperties.put("width", String.valueOf(width));
		
		double length = config.getWidthMin();   
		buildingPartProperties.put("length", String.valueOf(length));       
        
		
		return buildingPartProperties;
	}
	
	private void layoutBuildingParts(List<Node> buildingParts) {
		
		//TODO Layout Chimneys
			//X and Z Position

		// TODO Layout Floors
			//Y Position
			//Set Width
		
		//TODO Set Y Position of Chimneys
		
	}
	
	private void saveBuildingPartProperties(Node buildingPart) {
		
		HashMap<String, String> buildingPartProperties = nodeProperties.get(buildingPart.id());
		
		String height = buildingPartProperties.get("height");
		String color = buildingPartProperties.get("");
		
		connector.executeWrite(
		        String.format("MATCH (n) WHERE ID(n) = %d SET n.height = \'%s\',n.color = \'%s\'", buildingPart.id(),
		        		height, color));
	}
	
	
	
	
	
	//Helper
	
	
	private Node getSourceNode(Long elementID) {
		StatementResult sourceElements = connector.executeRead(
				" MATCH (n)-[:MAPPED]->(m) WHERE ID(m) = " + elementID +
				" RETURN n");
		
		return sourceElements.single().get("n").asNode();
	}	
	
	private List<Node> getNodesOfResult(StatementResult statementResult, String NodeReturnLabel){
		List<Node> nodeList = new ArrayList<Node>();
		
		statementResult.forEachRemaining((result) -> {
			nodeList.add(result.get(NodeReturnLabel).asNode());
		});
		
		return nodeList;
	}
	
    
    
}*/