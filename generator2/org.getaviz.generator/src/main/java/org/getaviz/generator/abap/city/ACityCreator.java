package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;
import org.neo4j.driver.v1.types.Node;

import java.util.ArrayList;
import java.util.List;

public class ACityCreator {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();


    public ACityCreator(SettingsConfiguration config) {
        this.config = config;
    }

    public ACityRepository createACityRepository(){

        ACityRepository repository = new ACityRepository();

        //Node modelNode = createModelNode();


        List<Node> sourcePackages = loadSourcePackages();
        List<ACityElement> packageDistricts = createPackageDistrictsElements(sourcePackages);
        repository.addElements(packageDistricts);


        /*
        List<Node> sourceTypes = loadSourceTypes(packageDistricts);

        List<ACityElement> Buildings = createPackageDistrictsElements(sourcePackages);
        repository.addElements(packageDistricts);
         */


        return repository;
    }

    private List<ACityElement> createPackageDistrictsElements(List<Node> sourcePackages) {
        List<ACityElement> packageDistricts = new ArrayList<>();

        for( Node sourcePackage: sourcePackages ) {
            ACityElement packageDistrict = new ACityElement((sourcePackage), ACityElement.ACityType.District);
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

    private List<Node> loadSourceTypes(List<ACityElement> packageDistricts ){
        List<Node> sourceTypes = new ArrayList<>();

        for( ACityElement packageDistrict : packageDistricts ) {

            connector.executeRead(
                    " MATCH (n)-[:CONTAINS]->(t:Type) WHERE ID(n) = " + packageDistrict.getId() +
                            " RETURN t"
            ).forEachRemaining((result) -> {
                Node sourceType = result.get("n").asNode();
                sourceTypes.add(sourceType);
            });

        }

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
