MATCH (n:Model)-[:CONTAINS*]->(d:District)-[:HAS]->(p:Position) 
WHERE n.building_type = 'original' 
SET d.aframe_code = '<a-box id="123" position="-1 0.5 -3" rotation="0 45 0" color="#4CC3D9">' + '</a-box>'
RETURN d