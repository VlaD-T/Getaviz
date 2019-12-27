CREATE (VISAP_T:Package { element_id : '1', object_name : '/GSA/VISAP_T', type : 'DEVC' })
CREATE (VISAP_T_TEST:Package { element_id : '2', object_name : '/GSA/VISAP_T_TEST', type : 'DEVC'})

CREATE (VISAP_T_REPORT2:Report:Type { element_id: '3', object_name: '/GSA/VISAP_T_TEST_REPORT2', type : 'REPS' })
CREATE (VISAP_T_CLASS:Class:Type {element_id: '4', object_name: '/GSA/VISAP_T_TEST_CLASS', type : 'CLASS' })
CREATE (VISAP_T_FUGR:FunctionGroup:Type { element_id : '5', object_name : '/GSA/VISAP_T_FUGR', type : 'FUGR' })
CREATE (VISAP_T_TABLE:Table:Type { element_id : '12' , object_name : '/GSA/VISAP_T_TABLE' , type : 'TABL' })

CREATE (VISAP_T_METH:Method {element_id: '10', object_name : '/GSA/VISAP_T_TEST_METHOD', type : 'METH' })
CREATE (VISAP_T_ATTR:Attribute {element_id: '11', object_name : '/GSA/VISAP_T_TEST_ATTRIBUTE', type : 'ATTR' })
CREATE (VISAP_T_FUMO:FunctionModule { element_id : '6', object_name : '/GSA/VISAP_T_FUMO', type : 'FUMO' })
CREATE (VISAP_T_FORM:FormRoutine { element_id : '7', object_name : '/GSA/VISAP_T_FORM', type : 'FORM' })

CREATE (VISAP_T_DOMA:Domain { element_id : '8', object_name : '/GSA/VISAP_T_DOMA', type : 'DOMA' })
CREATE (VISAP_T_DTEL:DataElement { element_id : '9', object_name : '/GSA/VISAP_T_DTEL', type : 'DTEL' })
CREATE (VISAP_T_STRUC:Structure { element_id : '13' , object_name : '/GSA/VISAP_T_STRUC' , type : 'STRU' })
CREATE (VISAP_T_TTYP:TableType { element_id : '14' , object_name : '/GSA/VISAP_T_TTYP' , type : 'TTYP' }) 

CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_TABLE)

CREATE (VISAP_T_TEST)-[:CONTAINS]->(VISAP_T_CLASS)
CREATE (VISAP_T_CLASS)-[:DECLARES]->(VISAP_T_METH)
CREATE (VISAP_T_CLASS)-[:DECLARES]->(VISAP_T_ATTR)

CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_REPORT2)
CREATE (VISAP_T_REPORT2)-[:DECLARES]->(VISAP_T_FORM)

CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_FUGR)
CREATE (VISAP_T_FUGR)-[:DECLARES]->(VISAP_T_FUMO)
