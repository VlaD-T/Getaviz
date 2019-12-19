package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.driver.v1.types.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ACityCreator {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    private ACityRepository repository;

    public ACityCreator(SettingsConfiguration config) {
        this.config = config;

        repository = new ACityRepository();
    }



    public ACityRepository createACityRepository(NodeRepository nodeRepository){

        //Packages
        Collection<Node> sourcePackages = nodeRepository.getNodesByLabel("Package");
        List<ACityElement> packageDistricts = createPackageDistrictsElements(sourcePackages);
        repository.addElements(packageDistricts);

        /*
        for( ACityElement packageDistrict : packageDistricts ) {

            createTypesForDistrict(packageDistrict, "Class");
            createTypesForDistrict(packageDistrict, "Report");
            createTypesForDistrict(packageDistrict, "FunctionGroup");
            createTypesForDistrict(packageDistrict, "Table");

        }*/

        return repository;
    }

    private void createTypesForDistrict(ACityElement packageDistrict, String sourceTypeLabel) {

        List<Node> sourceTypes = loadSourceTypes(packageDistrict, sourceTypeLabel);

        if( sourceTypes.size() != 0){
            ACityElement sourceTypeDistrict = new ACityElement(ACityElement.ACityType.District);
            sourceTypeDistrict.setParentElement(packageDistrict);
            repository.addElement(sourceTypeDistrict);

            List<ACityElement> typeBuildings = createTypeBuildingElements(sourceTypeDistrict, sourceTypes);
            repository.addElements(typeBuildings);


        }

    }


    private List<ACityElement> createTypeBuildingElements(ACityElement classDistrict, List<Node> sourceTypes) {
        List<ACityElement> buildings = new ArrayList<>();

        for( Node sourceType: sourceTypes ) {
            ACityElement building = new ACityElement(ACityElement.ACityType.Building);

            building.setSourceNodeID(sourceType.id());
            building.setParentElement(classDistrict);

            buildings.add(building);
        }

        return buildings;
    }


    private List<ACityElement> createPackageDistrictsElements(Collection<Node> sourcePackages) {
        List<ACityElement> packageDistricts = new ArrayList<>();

        for( Node sourcePackage: sourcePackages ) {
            ACityElement packageDistrict = new ACityElement(ACityElement.ACityType.District);

            packageDistrict.setSourceNodeID(sourcePackage.id());

            packageDistricts.add(packageDistrict);
        }

        return packageDistricts;
    }









    private List<Node> loadSourcePackages(){
        List<Node> sourcePackages = new ArrayList<>();

        connector.executeRead(
                "MATCH (n:Package) " +
                        "RETURN n"
        ).forEachRemaining((result) -> {
            Node sourcePackage = result.get("n").asNode();
            sourcePackages.add(sourcePackage);
        });

        return sourcePackages;
    }

    private List<Node> loadSourceTypes(ACityElement packageDistrict, String sourceTypeLabel){
        List<Node> sourceTypes = new ArrayList<>();

        connector.executeRead(
                " MATCH (n)-[:CONTAINS]->(t:" + sourceTypeLabel +") WHERE ID(n) = " + packageDistrict.getSourceNodeID() +
                        " RETURN t"
        ).forEachRemaining((result) -> {
            Node sourceType = result.get("t").asNode();
            sourceTypes.add(sourceType);
        });

        return sourceTypes;
    }





    /*
    private Node createModelNode(){
        connector.executeWrite("MATCH (n:City) DETACH DELETE n");

        Node modelNode = connector.addNode(
                String.format("CREATE (n:Model:ACity {date: \'%s\', building_type: \'%s\'})",
                        new GregorianCalendar().getTime().toString(), config.getBuildingTypeAsString()),"n");

        return modelNode;
    }
    */
}
