LOAD CSV WITH HEADERS FROM "file:///C:/Users/G530358/Desktop/Anwendungen/JetBrains/Projekte/GetavizABAP/generator2/org.getaviz.generator/src/test/neo4jexport/20200214_Test_TypeOf.csv"
AS row FIELDTERMINATOR ';'
MATCH (a:Elements {element_id: row.source_id}), (b:Elements {element_id: row.target_id})
CREATE (a)-[r:TYPEOF]->(b)