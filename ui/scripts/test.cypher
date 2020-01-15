RETURN EXISTS(parent {hash: "ID_736ff4e8c509af97b93144e21132e8b6f9810a26"}) 

MATCH (parent)-[:DECLARES|HAS|CONTAINS|DEPENDS_ON]->(child) WHERE parent.hash = "ID_736ff4e8c509af97b93144e21132e8b6f9810a26" RETURN child


MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child)
WHERE parent.hash = "ID_5fb552a76ef3c7ee67681d80e9797e088a6c9859" AND EXISTS(child.hash) RETURN COUNT(child) LIMIT 1


MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child)
WHERE parent.hash = "ID_5fb552a76ef3c7ee67681d80e9797e088a6c9859" AND EXISTS(child.hash) RETURN child LIMIT 1


MATCH (parent)-[:DECLARES|HAS|CONTAINS]->(child)
                                    WHERE parent.hash = "${entity.id}" 
                                    AND EXISTS(child.hash) 
                                    RETURN child
                                    

// recursive query:
// https://stackoverflow.com/questions/31079881/simple-recursive-cypher-query

MATCH (n {hash: "ID_6495ec2433cdc7c2d66b33a293fa6904ef2cfc18"})-[:DECLARES|HAS|CONTAINS *1..2]->(child)
WHERE EXISTS (child.hash)
RETURN child