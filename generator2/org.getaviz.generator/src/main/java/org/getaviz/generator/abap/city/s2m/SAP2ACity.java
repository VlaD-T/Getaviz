package org.getaviz.generator.abap.city.s2m;

import org.getaviz.generator.SettingsConfiguration;
import org.neo4j.driver.v1.StatementResult;

import java.util.GregorianCalendar;
import org.getaviz.generator.abap.city.Labels;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.database.DatabaseConnector;

public class SAP2ACity {
	private SettingsConfiguration config = SettingsConfiguration.getInstance();
	private Log log = LogFactory.getLog(this.getClass());
	private DatabaseConnector connector = DatabaseConnector.getInstance();

	public SAP2ACity() {
		log.info("SAP2ACity started");
		
		connector.executeWrite("MATCH (n:City) DETACH DELETE n"); 
		
		long model = connector.addNode(
			String.format("CREATE (n:Model:ACity {date: \'%s\', building_type: \'%s\'})",
				new GregorianCalendar().getTime().toString(), config.getBuildingTypeAsString()),"n").id();
		
		connector.executeRead(
			"MATCH (n:Package) " +			
			"RETURN n" 
		).forEachRemaining((result) -> {
			long sapPackage = result.get("n").asNode().id();
			sapPackageToDistrict(sapPackage, model);
		});
		
		log.info("SAP2ACity finished");
	}

	private Long sapPackageToDistrict(Long sapPackage, Long parent) {
		long district = connector.addNode(cypherCreateNode(parent,sapPackage,Labels.District.name()),"n").id();
		
		StatementResult subTypes = connector.executeRead(
			" MATCH (n)-[:CONTAINS]->(t:Type) WHERE ID(n) = " + sapPackage +		
			" RETURN t");
		
		subTypes.forEachRemaining((result) -> {
			structureToBuilding(result.get("t").asNode().id(), district);
		});
		
		
		return district;
	}
	
	

	private Long structureToBuilding(Long structure, Long parent) {
		long building = connector.addNode(cypherCreateNode(parent, structure, Labels.Building.name()),"n").id();
		
//		Elements of structure
		StatementResult elements = readStructureElements2(structure, parent);
		elements.forEachRemaining((result) -> {
			elementToBuildingPart(result.get("m").asNode().id(), building);
				});		

		return building;
	}
	
	private StatementResult readStructureElements2(Long structure, Long parent) {		
		return connector.executeRead(
				" MATCH (n)-[:DECLARES]->(m) WHERE ID(n) = " + structure +
				" RETURN m");
	}
	
	
	private void elementToBuildingPart(Long element, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, element, Labels.BuildingPart.name()));
	}
	
	
	private String cypherCreateNode(Long parent, Long visualizedNode, String label) {
		return String.format(
			"MATCH(parent),(s) WHERE ID(parent) = %d AND ID(s) = %d CREATE (parent)-[:CONTAINS]->(n:ACity:%s)-[:VISUALIZES]->(s)",
			parent, visualizedNode, label);
	}
}
