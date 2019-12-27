package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ACityBuildingLayout {

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityElement building;
    private Collection<ACityElement> floors;
    private Collection<ACityElement> chimneys;

    public ACityBuildingLayout(ACityElement building, Collection<ACityElement> floors, Collection<ACityElement> chimneys, SettingsConfiguration config) {
        this.config = config;

        this.building = building;
        this.floors = floors;
        this.chimneys = chimneys;
    }

    public void calculate(){

        setSizeOfChimneys();
        setSizeOfFloors();

        setPositionOfFloors();
        setPositionOfChimneys();

        setSizeOfBuilding();
        setPositionOfBuilding();

    }

    private void setPositionOfBuilding() {
        building.setXPosition(0.0);
        building.setYPosition(0.0);
        building.setZPosition(0.0);
    }

    private void setSizeOfBuilding() {
        Double floorHeightSum = calculateFloorHeightSum();
        Double biggestChimneyHeight = getBiggestChimneyHeight();
        Double groundAreaLength = calculateGroundAreaByChimneyAmount();

        building.setWidth(groundAreaLength);
        building.setLength(groundAreaLength);

        building.setHeight(floorHeightSum + biggestChimneyHeight);
    }

    private Double getBiggestChimneyHeight() {
        double biggestChimneyHeight = 0.0;
        for(ACityElement chimney : chimneys){
            double chimneyHeight = chimney.getHeight();
            if(chimneyHeight > biggestChimneyHeight){
                biggestChimneyHeight = chimneyHeight;
            }
        }
        return biggestChimneyHeight;
    }

    private void setPositionOfChimneys() {
        Double floorHeightSum = calculateFloorHeightSum();
        Double groundAreaLength = calculateGroundAreaByChimneyAmount();

        //TODO refactor names
        List<ACityElement> corner1 = new ArrayList<>();
        List<ACityElement> corner2 = new ArrayList<>();
        List<ACityElement> corner3 = new ArrayList<>();
        List<ACityElement> corner4 = new ArrayList<>();

        int chimneyCounter = 0;

        for(ACityElement chimney : chimneys){
            int counterRemainder = chimneyCounter % 4;
            switch (counterRemainder){
                case 0: corner1.add(chimney); break;
                case 1: corner2.add(chimney); break;
                case 2: corner3.add(chimney); break;
                case 3: corner4.add(chimney); break;
            }
            chimneyCounter++;
        }

        setPositionOfChimneysInCorner(corner1, groundAreaLength, floorHeightSum, -1, 1);
        setPositionOfChimneysInCorner(corner2, groundAreaLength, floorHeightSum, 1, 1);
        setPositionOfChimneysInCorner(corner3, groundAreaLength, floorHeightSum, 1, -1);
        setPositionOfChimneysInCorner(corner4, groundAreaLength, floorHeightSum, -1, -1);
    }

    private Double calculateFloorHeightSum() {
        double floorHeightSum = 1.0; //TODO config
        for(ACityElement floor : floors){
            double floorTopEdge = floor.getYPosition() + floor.getHeight();
            if(floorTopEdge > floorHeightSum){
                floorHeightSum = floorTopEdge;
            }
        }
        return floorHeightSum;
    }

    private void setPositionOfChimneysInCorner(List<ACityElement> corner, Double groundAreaLength, Double floorHeightSum, int cornerX, int cornerZ) {

        double chimneyXPosition = 0.0;
        double chimneyYPosition = 0.0;
        double chimneyZPosition = 0.0;

        int cornerCounter = 0;
        for(ACityElement chimney: corner){

            if(cornerCounter == 0){
                chimneyXPosition = (groundAreaLength / 2 - chimney.getWidth() / 2) * cornerX ;
                chimneyYPosition = floorHeightSum;
                chimneyZPosition = (groundAreaLength / 2 - chimney.getLength() / 2) * cornerZ;
            }

            chimney.setXPosition(chimneyXPosition);
            chimney.setYPosition(chimneyYPosition);
            chimney.setZPosition(chimneyZPosition);

            cornerCounter++;
            //upper left corner
            if(cornerX < 0 && cornerZ > 0){
                chimneyXPosition = chimneyXPosition + chimney.getWidth() + 0.0; //TODO config gap
            }
            //upper right corner
            if(cornerX > 0 && cornerZ > 0){
                chimneyZPosition = chimneyZPosition - chimney.getLength() + 0.0; //TODO config gap
            }
            //lower right corner
            if(cornerX > 0 && cornerZ < 0){
                chimneyXPosition = chimneyXPosition - chimney.getWidth() + 0.0; //TODO config gap
            }
            //lower left corner
            if(cornerX < 0 && cornerZ < 0){
                chimneyZPosition = chimneyZPosition + chimney.getLength() + 0.0; //TODO config gap
            }
        }
    }


    private void setPositionOfFloors() {
        int floorCounter = 0;
        for(ACityElement floor : floors){
            floorCounter++;

            Double floorSizeSum = (floorCounter - 1) * floor.getHeight();
            Double floorGapSum = floorCounter * 0.5; //TODO Config
            floor.setYPosition(floorSizeSum + floorGapSum);

            floor.setXPosition(0.0);
            floor.setZPosition(0.0);
        }
    }

    private void setSizeOfFloors() {
        Double groundAreaLength = calculateGroundAreaByChimneyAmount();

        for(ACityElement floor : floors){
            floor.setHeight(1); //TODO Config
            floor.setWidth(groundAreaLength);
            floor.setLength(groundAreaLength);
        }
    }

    private double calculateGroundAreaByChimneyAmount() {
        //TODO calculateByChimney width and length
        if (chimneys.size() < 2){
            return 2.0;
        }

        int chimneyAmount = chimneys.size();
        double chimneySurface = Math.sqrt(chimneyAmount);

        return Math.ceil(chimneySurface);
    }

    private void setSizeOfChimneys() {
        for(ACityElement chimney : chimneys){
            chimney.setHeight(1); //TODO Config
            chimney.setWidth(1); //TODO Config
            chimney.setLength(1); //TODO Config
        }
    }


}
