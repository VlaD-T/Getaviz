package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.database.DatabaseConnector;

public class ACityMetaphor {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;
    private DatabaseConnector connector = DatabaseConnector.getInstance();

    public ACityMetaphor(SettingsConfiguration config) {
        this.config = config;
    }

    public void createCityElements(){
        log.info("createCityElements started");

        log.info("createCityElements ended");
    }


    public void generate() {
        try {

            //ACityCreator

            //ACityDesigner

            //ACityLayouter



        } catch (Exception e) {
            log.error(e);
        }
    }


}
