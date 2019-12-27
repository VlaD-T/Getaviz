package org.getaviz.generator.abap.city;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.neo4j.driver.v1.types.Node;

public class ACityElement {

    public enum ACityType {
        City, District, Building, Floor, Chimney
    }

    private String hash;
    private Long sourceNodeID;

    private Node sourceNode;

    private List<ACityElement> subElements;
    private ACityElement parentElement;

    private ACityType type;

    private String color;

    private double height;
    private double width;
    private double length;

    private double xPosition;
    private double yPosition;
    private double zPosition;

    public ACityElement(ACityType type) {
        this.type = type;
        subElements = new ArrayList<>();

        UUID uuid = UUID.randomUUID();
        hash = "ID_" + uuid.toString();
    }

    public Long getSourceNodeID() {
        return sourceNode.id();
    }


    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }




    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getZPosition() {
        return zPosition;
    }

    public void setZPosition(double zPosition) {
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

    public Collection<ACityElement> getSubElements() {
        //TODO return copy
        return subElements;
    }

    public Collection<ACityElement> getSubElementsOfType(ACityType elementType) {
        List<ACityElement> subElementsOfType = new ArrayList<>();

        Collection<ACityElement> subElements = getSubElements();
        for(ACityElement element : subElements){

            if( element.getType() == elementType){
                subElementsOfType.add(element);
            }
        }
        return subElementsOfType;
    }

    public void addSubElement(ACityElement subElement) {
        this.subElements.add(subElement);
    }





    public String getHash() {
        return hash;
    }

    public ACityType getType() {
        return type;
    }
}
