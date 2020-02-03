CREATE (VISAP_T:Package { element_id : '1', object_name : '/GSA/VISAP_T', type : 'DEVC' })

CREATE (VISAP_T_REPORT2:Report:Type { element_id: '3', object_name: '/GSA/VISAP_T_TEST_REPORT2', type : 'REPS' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_REPORT2)
CREATE (VISAP_T_FORM:FormRoutine { element_id : '7', object_name : '/GSA/VISAP_T_FORM', type : 'FORM' })
CREATE (VISAP_T_REPORT2)-[:CONTAINS]->(VISAP_T_FORM)

CREATE (VISAP_T_FUGR:FunctionGroup:Type { element_id : '5', object_name : '/GSA/VISAP_T_FUGR', type : 'FUGR' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_FUGR)
CREATE (VISAP_T_FUMO:FunctionModule { element_id : '6', object_name : '/GSA/VISAP_T_FUMO', type : 'FUMO' })
CREATE (VISAP_T_FUGR)-[:CONTAINS]->(VISAP_T_FUMO)


CREATE (VISAP_T_TABLE:Table:Type { element_id : '12' , object_name : '/GSA/VISAP_T_TABLE' , type : 'TABL' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_TABLE)
CREATE (VISAP_T_TABLE_ELEMENT1:TableElement { element_id : '121' , object_name : '/GSA/VISAP_T_TABLE-E1' , type : 'COMP' })
CREATE (VISAP_T_TABLE)-[:CONTAINS]->(VISAP_T_TABLE_ELEMENT1)
CREATE (VISAP_T_TABLE_ELEMENT2:TableElement { element_id : '122' , object_name : '/GSA/VISAP_T_TABLE-E2' , type : 'COMP' })
CREATE (VISAP_T_TABLE)-[:CONTAINS]->(VISAP_T_TABLE_ELEMENT2)



CREATE (VISAP_T_DOMA:Domain { element_id : '8', object_name : '/GSA/VISAP_T_DOMA', type : 'DOMA' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_DOMA)

CREATE (VISAP_T_DTEL:DataElement { element_id : '9', object_name : '/GSA/VISAP_T_DTEL', type : 'DTEL' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_DTEL)
CREATE (VISAP_T_DTEL9:DataElement { element_id : '99', object_name : '/GSA/VISAP_T_DTEL9', type : 'DTEL' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_DTEL9)

CREATE (VISAP_T_STRUC:Structure { element_id : '13' , object_name : '/GSA/VISAP_T_STRUC' , type : 'STRU' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_STRUC)
CREATE (VISAP_T_STRUC_ELEMENT1:StructureElement { element_id : '131' , object_name : '/GSA/VISAP_T_STRUC-E1' , type : 'COMP' })
CREATE (VISAP_T_STRUC)-[:CONTAINS]->(VISAP_T_STRUC_ELEMENT1)
CREATE (VISAP_T_STRUC_ELEMENT2:StructureElement { element_id : '132' , object_name : '/GSA/VISAP_T_STRUC-E2' , type : 'COMP' })
CREATE (VISAP_T_STRUC)-[:CONTAINS]->(VISAP_T_STRUC_ELEMENT2)
CREATE (VISAP_T_STRUC_ELEMENT3:StructureElement { element_id : '133' , object_name : '/GSA/VISAP_T_STRUC-E3' , type : 'COMP' })
CREATE (VISAP_T_STRUC)-[:CONTAINS]->(VISAP_T_STRUC_ELEMENT3)
CREATE (VISAP_T_STRUC_ELEMENT4:StructureElement { element_id : '134' , object_name : '/GSA/VISAP_T_STRUC-E4' , type : 'COMP' })
CREATE (VISAP_T_STRUC)-[:CONTAINS]->(VISAP_T_STRUC_ELEMENT4)

CREATE (VISAP_T_TTYP1:TableType { element_id : '14' , object_name : '/GSA/VISAP_T_TTYP1' , type : 'TTYP' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_TTYP1)
CREATE (VISAP_T_TTYP1)-[:TYPEOF]->(VISAP_T_STRUC)

CREATE (VISAP_T_TTYP2:TableType { element_id : '34' , object_name : '/GSA/VISAP_T_TTYP2' , type : 'TTYP' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_TTYP2)
CREATE (VISAP_T_TTYP2)-[:TYPEOF]->(VISAP_T_TABLE)

CREATE (VISAP_T_TTYP3:TableType { element_id : '35' , object_name : '/GSA/VISAP_T_TTYP3' , type : 'TTYP' })
CREATE (VISAP_T)-[:CONTAINS]->(VISAP_T_TTYP3)
CREATE (VISAP_T_TTYP3)-[:TYPEOF]->(VISAP_T_DTEL)



CREATE (VISAP_T_TEST:Package { element_id : '2', object_name : '/GSA/VISAP_T_TEST', type : 'DEVC'})

CREATE (VISAP_T_CLASS:Class:Type {element_id: '4', object_name: '/GSA/VISAP_T_TEST_CLASS', type : 'CLASS' })
CREATE (VISAP_T_TEST)-[:CONTAINS]->(VISAP_T_CLASS)
CREATE (VISAP_T_METH:Method {element_id: '10', object_name : '/GSA/VISAP_T_TEST_METHOD', type : 'METH' })
CREATE (VISAP_T_CLASS)-[:CONTAINS]->(VISAP_T_METH)
CREATE (VISAP_T_METH1:Method {element_id: '110', object_name : '/GSA/VISAP_T_TEST_METHOD1', type : 'METH' })
CREATE (VISAP_T_CLASS)-[:CONTAINS]->(VISAP_T_METH1)
CREATE (VISAP_T_METH2:Method {element_id: '111', object_name : '/GSA/VISAP_T_TEST_METHOD2', type : 'METH' })
CREATE (VISAP_T_CLASS)-[:CONTAINS]->(VISAP_T_METH2)
CREATE (VISAP_T_ATTR:Attribute {element_id: '11', object_name : '/GSA/VISAP_T_TEST_ATTRIBUTE', type : 'ATTR' })
CREATE (VISAP_T_CLASS)-[:CONTAINS]->(VISAP_T_ATTR)


CREATE (VISAP_T_INTERFACE:Interface:Type {element_id: '24', object_name: '/GSA/VISAP_T_TEST_INTERFACE', type : 'INTF' })
CREATE (VISAP_T_TEST)-[:CONTAINS]->(VISAP_T_INTERFACE)
CREATE (VISAP_T_INTF_METH1:Method {element_id: '241', object_name : '/GSA/VISAP_T_TEST_INTF_METHOD1', type : 'METH' })
CREATE (VISAP_T_INTERFACE)-[:CONTAINS]->(VISAP_T_INTF_METH1)
CREATE (VISAP_T_INTF_ATTR1:Attribute {element_id: '242', object_name : '/GSA/VISAP_T_TEST_INTF_ATTRIBUTE1', type : 'ATTR' })
CREATE (VISAP_T_INTERFACE)-[:CONTAINS]->(VISAP_T_INTF_ATTR1)
CREATE (VISAP_T_INTF_ATTR2:Attribute {element_id: '243', object_name : '/GSA/VISAP_T_TEST_INTF_ATTRIBUTE2', type : 'ATTR' })
CREATE (VISAP_T_INTERFACE)-[:CONTAINS]->(VISAP_T_INTF_ATTR2)
CREATE (VISAP_T_INTF_ATTR3:Attribute {element_id: '244', object_name : '/GSA/VISAP_T_TEST_INTF_ATTRIBUTE3', type : 'ATTR' })
CREATE (VISAP_T_INTERFACE)-[:CONTAINS]->(VISAP_T_INTF_ATTR3)
















