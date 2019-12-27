package org.getaviz.generator.tests.acity;

import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.abap.city.ACityBuildingLayout;
import org.getaviz.generator.abap.city.ACityElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingLayoutTest {

    private static SettingsConfiguration config = SettingsConfiguration.getInstance();

    private static ACityElement building;
    private static List<ACityElement> floors;
    private static List<ACityElement> chimneys;

    @BeforeAll
    static void setup() {
        building = new ACityElement(ACityElement.ACityType.Building);
        floors = new ArrayList<>();
        chimneys = new ArrayList<>();

        //8 floors
        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));

        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));
        floors.add(new ACityElement(ACityElement.ACityType.Floor));

        //10 chimneys
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));

        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));
        chimneys.add(new ACityElement((ACityElement.ACityType.Chimney)));


        ACityBuildingLayout buildingLayout = new ACityBuildingLayout(building, floors, chimneys, config);
        buildingLayout.calculate();
    }

    @Test
    void floorSize() {
        //groundArea = Ceil(Sqrt(10)) = 4
        for(ACityElement floor :  floors){
            assertEquals(4, floor.getWidth());
            assertEquals(4, floor.getLength());
            assertEquals(1, floor.getHeight());
        }
    }

    @Test
    void chimneySize(){
        for(ACityElement chimney : chimneys){
            assertEquals(1, chimney.getWidth());
            assertEquals(1, chimney.getLength());
            assertEquals(1, chimney.getHeight());
        }
    }

    @Test
    void floorPosition(){
        int floorCounter = 0;
        for(ACityElement floor :  floors){
            floorCounter++;

            if(floorCounter == 1){
                //0 x floor.height  + 1 x floor.gap
                //0 x 1             + 1 x 0.5
                assertEquals(0.5, floor.getYPosition());
            }
            if(floorCounter == 5){
                //4 x floor.height  + 5 x floor.gap
                //4 x 1             + 5 x 0.5
                assertEquals(6.5, floor.getYPosition());
            }
            if(floorCounter == 8){
                //7 x floor.height  + 8 x floor.gap
                //7 x 1             + 8 x 0.5
                assertEquals(11, floor.getYPosition());
            }

            assertEquals(0.0, floor.getXPosition());
            assertEquals(0.0, floor.getZPosition());
        }
    }

    @Test
    void chimneyPosition(){
        //groundAreaLength = roundUp( Sqrt( chimney.size ))
        //groundAreaLength = roundUp( Sqrt( 10 ))
        //groundAreaLength = 4

        int chimneyCounter = 0;
        for(ACityElement chimney :  chimneys){
            chimneyCounter++;

            /*  X---
                ----
                ----
                ---- */
            if(chimneyCounter == 1){
                assertEquals(-1.5, chimney.getXPosition());
                assertEquals(1.5, chimney.getZPosition());
            }
            /*  O--X
                ----
                ----
                ---- */
            if(chimneyCounter == 1.5){
                assertEquals(1.5, chimney.getXPosition());
                assertEquals(1.5, chimney.getZPosition());
            }
            /*  O--O
                ----
                ----
                ---X */
            if(chimneyCounter == 3){
                assertEquals(1.5, chimney.getXPosition());
                assertEquals(-1.5, chimney.getZPosition());
            }
            /*  O--O
                ----
                ----
                X--O */
            if(chimneyCounter == 4){
                assertEquals(-1.5, chimney.getXPosition());
                assertEquals(-1.5, chimney.getZPosition());
            }

            /*  OX-O
                ----
                ----
                O--O */
            if(chimneyCounter == 5){
                assertEquals(-0.5, chimney.getXPosition());
                assertEquals(1.5, chimney.getZPosition());
            }
            /*  OO-O
                ---X
                ----
                O--O */
            if(chimneyCounter == 6){
                assertEquals(1.5, chimney.getXPosition());
                assertEquals(0.5, chimney.getZPosition());
            }
            /*  OO-O
                ---O
                ----
                O-XO */
            if(chimneyCounter == 7){
                assertEquals(0.5, chimney.getXPosition());
                assertEquals(-1.5, chimney.getZPosition());
            }
            /*  OO-O
                ---O
                X---
                O-OO */
            if(chimneyCounter == 8){
                assertEquals(-1.5, chimney.getXPosition());
                assertEquals(-0.5, chimney.getZPosition());
            }

            //8 x floor.height  + 8 x floor.gap
            //8 x 1             + 8 x 0.5
            assertEquals(12, chimney.getYPosition());
        }
    }

    @Test
    void buildingSize(){
        //groundAreaLength = roundUp( Sqrt( chimney.size ))
        //groundAreaLength = roundUp( Sqrt( 10 ))
        //groundAreaLength = 4

        assertEquals(4.0, building.getWidth());
        assertEquals(4.0, building.getLength());

        //8 x floor.height  + 8 x floor.gap + chimney.height
        //8 x 1             + 8 x 0.5       + 1
        assertEquals(13.0, building.getHeight());
    }

    @Test
    void buildingPosition(){
        assertEquals(0.0, building.getXPosition());
        assertEquals(0.0, building.getYPosition());
        assertEquals(0.0, building.getZPosition());
    }


}
