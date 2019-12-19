package org.getaviz.generator.abap.city;

import java.util.ArrayList;
import java.util.List;
import org.neo4j.driver.v1.types.Node;

public class ACityElement {

    private String hash;
    private Long sourceNodeID;


    private List<ACityElement> subElements;
    private ACityElement parentElement;

    private ACityType type;

    private String color;

    private float height;
    private float width;
    private float length;

    private float xPosition;
    private float yPosition;
    private float zPosition;

    public ACityElement(ACityType type) {
        this.type = type;

        subElements = new ArrayList<ACityElement>();
    }

    public Long getSourceNodeID() {
        return sourceNodeID;
    }

    public void setSourceNodeID(Long sourceNodeID) {
        this.sourceNodeID = sourceNodeID;
    }

    public enum ACityType {
        City, District, Building, BuildingPart,
        Method, Field, FormRoutine, FunctionModule, AbapStructure, Domain, DataElement, TableType
    }


    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getXPosition() {
        return xPosition;
    }

    public void setXPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getYPosition() {
        return yPosition;
    }

    public void setYPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getZPosition() {
        return zPosition;
    }

    public void setZPosition(float zPosition) {
        this.zPosition = zPosition;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ACityElement getParentElement() {
        return parentElement;
    }

    public void setParentElement(ACityElement parentElement) {
        this.parentElement = parentElement;
    }

    public List<ACityElement> getSubElements() {
        //TODO return copy
        return subElements;
    }

    public void addSubElement(ACityElement subElement) {
        this.subElements.add(subElement);
    }





    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public ACityType getType() {
        return type;
    }
}
