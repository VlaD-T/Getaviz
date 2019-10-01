package org.getaviz.generator.mockups;

import java.io.File;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ABAPmock extends Mockup {

	public void setupDatabase(String directory) {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(directory))
				.setConfig(bolt.type, "BOLT").setConfig(bolt.enabled, "true")
				.setConfig(bolt.listen_address, "localhost:11003").newGraphDatabase();
		registerShutdownHook(graphDb);
		connector = DatabaseConnector.getInstance("bolt://localhost:11003");
		resetDatabase();
		runCypherScript("SAP.cypher");
	}
}
