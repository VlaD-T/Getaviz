package org.getaviz.generator.abap.city.s2m;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.Labels;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;

import java.util.GregorianCalendar;
import org.getaviz.generator.SettingsConfiguration.BuildingType;
import org.getaviz.generator.SettingsConfiguration.ClassElementsModes;
import org.getaviz.generator.SettingsConfiguration.Original.BuildingMetric;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.database.DatabaseConnector;

public class SAP2ACity {
	private SettingsConfiguration config = SettingsConfiguration.getInstance();
	private Log log = LogFactory.getLog(this.getClass());
	private DatabaseConnector connector = DatabaseConnector.getInstance();

	public SAP2ACity() {
		log.info("SAP2ABAP_City started");
		
		connector.executeWrite("MATCH (n:City) DETACH DELETE n"); 
		
		long model = connector.addNode(
			String.format("CREATE (n:Model:City {date: \'%s\', building_type: \'%s\'})",
				new GregorianCalendar().getTime().toString(), config.getBuildingTypeAsString()),"n").id();
		
		connector.executeRead(
			"MATCH (n:Package) " +
			"WHERE NOT (n)<-[:CONTAINS]-(:Package) " + 
			"RETURN n" 
		).forEachRemaining((result) -> {
			long namespace = result.get("n").asNode().id();
			namespaceToDistrict(namespace, model);
		});
		
		log.info("SAP2ABAP_City finished");
	}

	private Long namespaceToDistrict(Long namespace, Long parent) {
		long district = connector.addNode(cypherCreateNode(parent,namespace,Labels.District.name()),"n").id();
		
		StatementResult subPackages = connector.executeRead(
			" MATCH (n)-[:CONTAINS]->(p:Package) WHERE ID(n) = " + namespace +
			" RETURN p");
		
		subPackages.forEachRemaining((result) -> {
			namespaceToDistrict(result.get("p").asNode().id(), district);
		});
		
		StatementResult subTypes = connector.executeRead(
			" MATCH (n)-[:CONTAINS]->(t:Type) WHERE ID(n) = " + namespace +
			" AND EXISTS(t.hash)" +
			" AND (t:Class OR t:Interface OR t:Report OR t:Functiongroup OR t:Table) " +
			" AND NOT t:Inner RETURN t");
		
		subTypes.forEachRemaining((result) -> {
			structureToBuilding(result.get("t").asNode().id(), district);
		});
		
		
		return district;
	}

	private Long structureToBuilding(Long structure, Long parent) {
		long building = connector.addNode(cypherCreateNode(parent, structure, Labels.Building.name()),"n").id();
		
		StatementResult methods = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(m:Method) WHERE ID(n) = " + structure +
			" AND EXISTS(m.hash) RETURN m");
		StatementResult attributes = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(a:Field) WHERE ID(n) = " + structure +
			" AND EXISTS(a.hash) RETURN a");
		StatementResult formroutines = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(fo:Formroutine) WHERE ID(n) = " + structure +
			" AND EXISTS(fo.hash) RETURN fo");
		StatementResult functionmodules = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(fu:Functionmodule) WHERE ID(n) = " + structure +
			" AND EXISTS(fu.hash) RETURN fu");		
		StatementResult structures = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(st:Structure) WHERE ID(n) = " + structure +
			" AND EXISTS(st.hash) RETURN st");
		StatementResult domains = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(do:Domain) WHERE ID(n) = " + structure +
			" AND EXISTS(do.hash) RETURN do");
		StatementResult dataelements = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(de:Dataelement) WHERE ID(n) = " + structure +
			" AND EXISTS(de.hash) RETURN de");
		StatementResult tabletypes = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(tt:Tabletype) WHERE ID(n) = " + structure +
			" AND EXISTS(tt.hash) RETURN tt");
		//Table Elements
		//Structur Elements
		
		methods.forEachRemaining((result) -> {
			methodToBuildingSegment(result.get("m").asNode().id(), building);
				});
		attributes.forEachRemaining((result) -> {
			attributeToBuildingSegment(result.get("a").asNode().id(), building);
				});
		formroutines.forEachRemaining((result) -> {
			formroutineToBuildingSegment(result.get("fo").asNode().id(), building);
				});
		functionmodules.forEachRemaining((result) -> {
			functionmodulesToBuildingSegment(result.get("fu").asNode().id(), building);
				});		
		structures.forEachRemaining((result) -> {
			structuresToBuildingSegment(result.get("st").asNode().id(), building);
				});
		domains.forEachRemaining((result) -> {
			domainsToBuildingSegment(result.get("do").asNode().id(), building);
				});
		dataelements.forEachRemaining((result) -> {
			dataelementsToBuildingSegment(result.get("de").asNode().id(), building);
				});
		tabletypes.forEachRemaining((result) -> {
			tabletypesToBuildingSegment(result.get("tt").asNode().id(), building);
				});
		
		
		StatementResult subStructures = connector.executeRead(
			" MATCH (n)-[:DECLARES]->(t:Type:Inner) WHERE ID(n) = " + structure +
			" AND EXISTS(t.hash) RETURN t");
		
		subStructures.forEachRemaining((result) -> {
			structureToBuilding(result.get("t").asNode().id(), parent);
		});
		
		return building;
	}

	private void methodToBuildingSegment(Long method, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, method, Labels.BuildingSegment.name()));
	}

	private void methodToFloor(Long method, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, method, Labels.BuildingSegment.name() + ":" + Labels.Floor.name()));
	}

	private void attributeToBuildingSegment(Long attribute, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, attribute, Labels.BuildingSegment.name()));
	}

	private void attributeToChimney(Long attribute, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, attribute, Labels.BuildingSegment.name() + ":" + Labels.Chimney.name()));
	}
	
	private void formroutineToBuildingSegment(Long formroutine, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, formroutine, Labels.BuildingSegment.name()));
	}
	private void functionmodulesToBuildingSegment(Long functionmodule, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, functionmodule, Labels.BuildingSegment.name()));
	}
	private void tablesToBuildingSegment(Long table, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, table, Labels.BuildingSegment.name()));
	}
	private void structuresToBuildingSegment(Long structure, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, structure, Labels.BuildingSegment.name()));
	}
	private void domainsToBuildingSegment(Long domain, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, domain, Labels.BuildingSegment.name()));
	}
	private void dataelementsToBuildingSegment(Long dataelement, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, dataelement, Labels.BuildingSegment.name()));
	}
	private void tabletypesToBuildingSegment(Long tabletype, Long parent) {
		connector.executeWrite(cypherCreateNode(parent, tabletype, Labels.BuildingSegment.name()));
	}
	
	private String cypherCreateNode(Long parent, Long visualizedNode, String label) {
		return String.format(
			"MATCH(parent),(s) WHERE ID(parent) = %d AND ID(s) = %d CREATE (parent)-[:CONTAINS]->(n:City:%s)-[:VISUALIZES]->(s)",
			parent, visualizedNode, label);
	}
}
