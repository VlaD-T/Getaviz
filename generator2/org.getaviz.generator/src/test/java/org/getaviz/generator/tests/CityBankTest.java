package org.getaviz.generator.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.SettingsConfiguration.Metaphor;
import org.getaviz.generator.city.m2m.ABAP_City2ABAP_City;
import org.getaviz.generator.city.s2m.JQA2City;
import org.getaviz.generator.city.s2m.SAP2ABAP_City;
import org.getaviz.generator.city.m2m.City2City;
import org.getaviz.generator.database.DatabaseConnector;
import org.getaviz.generator.mockups.Bank;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

public class CityBankTest {
	
	private static SettingsConfiguration config = SettingsConfiguration.getInstance();

	static DatabaseConnector connector;
	static Bank mockup = new Bank();

	@BeforeAll
	static void setup() {
		mockup.setupDatabase("./test/databases/CityBankTest.db");
		mockup.loadProperties("CityBankTest.properties");
		connector = mockup.getConnector();
		
		new SAP2ABAP_City();
//		new JQA2City();
//		new ABAP_City2ABAP_City();
//		new City2City();
	}
	
//	@BeforeAll //SAP
//	static void setup() {
//		mockup.setupDatabase("./test/databases/CityBankTest.db");
//		mockup.loadProperties("CityBankTest.properties");
//		connector = mockup.getConnector();
//
//		new SAP2ABAP_City();
//		new City2City();
//	}
	
	@AfterAll
	static void close() {
		mockup.close();
	}

	@Test
	void numberOfVisualizedPackages() {
		Record result = connector
				.executeRead("MATCH (district:District)-[:VISUALIZES]->(:Package) RETURN count(district) AS result")
				.single();
		int numberOfVisualizedPackages = result.get("result").asInt();
		System.out.println("Pakete:" + result);
//		assertEquals(3, numberOfVisualizedPackages);
	}
	
//	Probe Report
	@Test
	void numberOfVisualizedReports() {
		Record result = connector
				.executeRead("MATCH (building:Building)-[:VISUALIZES]->(:Report) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedReports = result.get("result").asInt();
		System.out.println("Reports:" + result);
	}
	@Test
	void numberOfVisualizedTypes() {
		Record result = connector
				.executeRead("MATCH (building:Building)-[:VISUALIZES]->(:Type) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedTypes = result.get("result").asInt();
//		assertEquals(4, numberOfVisualizedTypes);
		System.out.println("Typen:" + result);
	}
	
	@Test
	void numberOfVisualizedClasses() {
		Record result = connector
				.executeRead("MATCH (building:Building)-[:VISUALIZES]->(:Class) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedClasses = result.get("result").asInt();
		System.out.println("Klassen:" + result);
//		assertEquals(0, test);
	}
	
	@Test
	void numberOfVisualizedFuGr() {
		Record result = connector
				.executeRead("MATCH (building:Building)-[:VISUALIZES]->(:Functiongroup) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedFuGr = result.get("result").asInt();
		System.out.println("Funktionsgruppen:" + result);
	}
	
	@Test
	void numberOfVisualizedDataObjects() {
		Record result = connector
				.executeRead("MATCH (building:Building)-[:VISUALIZES]->(:DataDictionary) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedDataObjects = result.get("result").asInt();
		System.out.println("DataDictionary:" + result);
	}
	
	@Test
	void numberOfVisualizedForms() {
		Record result = connector
		.executeRead("MATCH (building:BuildingSegment)-[:VISUALIZES]->(:Formroutine) RETURN count(building) AS result")
		.single();
//		StatementResult result = connector
//				.executeRead("MATCH p=(d:District)-[:CONTAINS*]->(b:Building) RETURN p AS result");
//		"MATCH p=(n:Model:City)-[:CONTAINS*]->(m:District) RETURN p"
//		(VISAP_T_REPORT2)-[:DECLARES]->(VISAP_T_FORM)
//		int numberOfVisualizedForms = result.get("result").asInt();
		System.out.println("Formroutinen:" + result);
	}
	
	@Test
	void numberOfVisualizedMethods() {
		Record result = connector
				.executeRead("MATCH (building:BuildingSegment)-[:VISUALIZES]->(:Method) RETURN count(building) AS result")
				.single();
		int numberOfVisualizedMethods = result.get("result").asInt();
		System.out.println("Methoden:" + result);
	}
	
	

//	@Test
//	void layoutAlgorithmPackage() {
//		String hash = "ID_4481fcdc97864a546f67c76536e0308a3058f75d";
//		Record result = connector.executeRead(
//				"MATCH (:Package {hash: '" + hash + "'})<-[:VISUALIZES]-(:District)-[:HAS]->(position:Position) "
//						+ "RETURN position.x as x, position.y as y, position.z as z")
//				.single();
//		double x = result.get("x").asDouble();
//		double y = result.get("y").asDouble();
//		double z = result.get("z").asDouble();
////		assertEquals(9.5, x);
////		assertEquals(2.5, y);
////		assertEquals(11.5, z);
//	}
//
//	@Test
//	void layoutAlgorithmClass() {
//		String hash = "ID_26f25e4da4c82dc2370f3bde0201e612dd88c04c";
//		Record result = connector.executeRead(
//				"MATCH (:Type {hash: '" + hash + "'})<-[:VISUALIZES]-(:Building)-[:HAS]->(position:Position) "
//						+ "RETURN position.x as x, position.y as y, position.z as z")
//				.single();
//		double x = result.get("x").asDouble();
//		double y = result.get("y").asDouble();
//		double z = result.get("z").asDouble();
////		assertEquals(9.5, x);
////		assertEquals(4, y);
////		assertEquals(13.5, z);
//	}
//
//	@Test
//	void classMembers() {
//		String hash = "ID_26f25e4da4c82dc2370f3bde0201e612dd88c04c";
//		Record result = connector.executeRead(
//				"MATCH (building:Building)-[:VISUALIZES]->(:Type {hash: '" + hash + "'}) "
//						+ "RETURN building.height as height, building.length as length, building.width as width")
//				.single();
//		double height = result.get("height").asDouble();
//		double length = result.get("length").asDouble();
//		double width = result.get("width").asDouble();
////		assertEquals(2.0, height);
////		assertEquals(1.0, length);
////		assertEquals(1.0, width);
//	}
}
