package org.getaviz.generator.abap.city;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.getaviz.generator.SettingsConfiguration;
import org.getaviz.generator.city.m2m.CityKDTree;
import org.getaviz.generator.city.m2m.CityKDTreeNode;
import org.getaviz.generator.city.m2m.Rectangle;

import java.util.*;

public class ACityDistrictLayout {
    //TODO Refactor, generalize and maybe reimplement

    private Log log = LogFactory.getLog(this.getClass());
    private SettingsConfiguration config;

    private ACityElement district;
    private Collection<ACityElement> subElements;

    private Map<Rectangle, ACityElement> rectangleElementsMap;

    public ACityDistrictLayout(ACityElement district, Collection<ACityElement> subElements, SettingsConfiguration config) {
        this.config = config;

        this.district = district;
        this.subElements = subElements;

        rectangleElementsMap = new HashMap<>();
    }

    public void calculate(){

        Rectangle coveringRectangle = arrangeSubElements(subElements);

        setSizeOfDistrict(coveringRectangle);
        setPositionOfDistrict(coveringRectangle);

    }


    private void setSizeOfDistrict(Rectangle coveringRectangle) {
        district.setWidth(coveringRectangle.getWidth());
        district.setLength(coveringRectangle.getLength());
        district.setHeight(0.2); //TODO config
    }

    private void setPositionOfDistrict(Rectangle coveringRectangle) {
        district.setXPosition(coveringRectangle.getCenterX());
        district.setYPosition(district.getHeight() / 2);
        district.setZPosition(coveringRectangle.getCenterY());
    }


    private void setNewPositionFromNode(Rectangle rectangle, CityKDTreeNode fitNode) {
        ACityElement element = rectangleElementsMap.get(rectangle);

        double xPosition = fitNode.getRectangle().getCenterX();// - config.getBuildingHorizontalGap() / 2;
        double xPositionDelta = xPosition - element.getXPosition();
        element.setXPosition(xPosition);

        double yPosition = element.getYPosition() + 0.2; //TODO config
        double yPositionDelta = yPosition - element.getYPosition();
        element.setYPosition(yPosition);

        double zPosition = fitNode.getRectangle().getCenterY();//- config.getBuildingHorizontalGap() / 2;
        double zPositionDelta = zPosition - element.getZPosition();
        element.setZPosition(zPosition);

        Collection<ACityElement> subElements = element.getSubElements();
        if(!subElements.isEmpty()){
            adjustPositionsOfSubSubElements(subElements, xPositionDelta, yPositionDelta, zPositionDelta);
        }
    }



    private void adjustPositionsOfSubSubElements(Collection<ACityElement> elements, double parentX, double parentY, double parentZ) {
        for (ACityElement element : elements) {

            double centerX = element.getXPosition();
            double centerY = element.getYPosition();
            double centerZ = element.getZPosition();

            double newXPosition = centerX + parentX ; // TODO + config.getBuildingHorizontalMargin();
            double newYPosition = centerY + parentY ; // TODO + config.getBuildingVerticalMargin();
            double newZPosition = centerZ + parentZ ; // TODO + config.getBuildingHorizontalMargin();

            element.setXPosition(newXPosition);
            element.setYPosition(newYPosition);
            element.setZPosition(newZPosition);

            Collection<ACityElement> subElements = element.getSubElements();
            if(!subElements.isEmpty()){
                adjustPositionsOfSubSubElements(subElements, parentX, parentY, parentZ);
            }
        }
    }







    /*
        Copied from CityLayout
     */


    private Rectangle arrangeSubElements(Collection<ACityElement> subElements){

        Rectangle docRectangle = calculateMaxAreaRoot(subElements);
        CityKDTree ptree = new CityKDTree(docRectangle);

        Rectangle covrec = new Rectangle();

        List<Rectangle> elements = createRectanglesOfElements(subElements);
        Collections.sort(elements);
        Collections.reverse(elements);

        // algorithm
        for (Rectangle el : elements) {
            Map<CityKDTreeNode, Double> preservers = new LinkedHashMap<>();
            Map<CityKDTreeNode, Double> expanders = new LinkedHashMap<>();
            CityKDTreeNode targetNode = new CityKDTreeNode();
            CityKDTreeNode fitNode = new CityKDTreeNode();

            List<CityKDTreeNode> pnodes = ptree.getFittingNodes(el);

            // check all empty leaves: either they extend COVREC (->expanders) or it doesn't
            // change (->preservers)
            for (CityKDTreeNode pnode : pnodes) {
                sortEmptyLeaf(pnode, el, covrec, preservers, expanders);
            }

            // choose best-fitting pnode
            if (!preservers.isEmpty()) {
                targetNode = bestFitIsPreserver(preservers.entrySet());
            } else {
                targetNode = bestFitIsExpander(expanders.entrySet());
            }

            // modify targetNode if necessary
            if (targetNode.getRectangle().getWidth() == el.getWidth()
                    && targetNode.getRectangle().getLength() == el.getLength()) { // this if could probably be skipped,
                // trimmingNode() always returns
                // fittingNode
                fitNode = targetNode;
            } else {
                fitNode = trimmingNode(targetNode, el);
            }

            // set fitNode as occupied
            fitNode.setOccupied();

            // give Entity it's Position
            setNewPositionFromNode(el, fitNode);

            // if fitNode expands covrec, update covrec
            if (fitNode.getRectangle().getBottomRightX() > covrec.getBottomRightX()
                    || fitNode.getRectangle().getBottomRightY() > covrec.getBottomRightY()) {
                updateCovrec(fitNode, covrec);
            }
        }

        return covrec;
    }

    private List<Rectangle> createRectanglesOfElements(Collection<ACityElement> elements) {
        List<Rectangle> rectangles = new ArrayList<>();

        for (ACityElement element : elements) {
            double width = element.getWidth();
            double length = element.getLength();

            Rectangle rectangle = new Rectangle(0, 0, width + config.getBuildingHorizontalGap(),
                    length + config.getBuildingHorizontalGap(), 1);
            rectangles.add(rectangle);
            rectangleElementsMap.put(rectangle, element);
        }
        return rectangles;
    }


    private Rectangle calculateMaxAreaRoot(Collection<ACityElement> elements) {
        double sum_width = 0;
        double sum_length = 0;
        for (ACityElement element : elements) {
            sum_width += element.getWidth() + config.getBuildingHorizontalGap();
            sum_length += element.getLength() + config.getBuildingHorizontalGap();
        }
        return new Rectangle(0, 0, sum_width, sum_length, 1);
    }




    private void sortEmptyLeaf(CityKDTreeNode pnode, Rectangle el, Rectangle covrec,
                               Map<CityKDTreeNode, Double> preservers, Map<CityKDTreeNode, Double> expanders) {
        // either element fits in current bounds (->preservers) or it doesn't
        // (->expanders)
        double nodeUpperLeftX = pnode.getRectangle().getUpperLeftX();
        double nodeUpperLeftY = pnode.getRectangle().getUpperLeftY();
        double nodeNewBottomRightX = nodeUpperLeftX + el.getWidth(); // expected BottomRightCorner, if el was insert
        // into pnode
        double nodeNewBottomRightY = nodeUpperLeftY + el.getLength(); // this new corner-point is compared with covrec

        if (nodeNewBottomRightX <= covrec.getBottomRightX() && nodeNewBottomRightY <= covrec.getBottomRightY()) {
            double waste = pnode.getRectangle().getArea() - el.getArea();
            preservers.put(pnode, waste);
        } else {
            double ratio = ((Math.max(nodeNewBottomRightX, covrec.getBottomRightX()))
                    / (Math.max(nodeNewBottomRightY, covrec.getBottomRightY())));
            expanders.put(pnode, ratio);
        }
    }

    private CityKDTreeNode bestFitIsPreserver(Set<Map.Entry<CityKDTreeNode, Double>> entrySet) {
        // determines which entry in Set has the lowest value of all
        double lowestValue = -1;
        CityKDTreeNode targetNode = new CityKDTreeNode();
        for (Map.Entry<CityKDTreeNode, Double> entry : entrySet) {
            if (entry.getValue() < lowestValue || lowestValue == -1) {
                lowestValue = entry.getValue();
                targetNode = entry.getKey();
            }
        }
        return targetNode;
    }

    private CityKDTreeNode bestFitIsExpander(Set<Map.Entry<CityKDTreeNode, Double>> entrySet) {
        double closestTo = 1;
        double lowestDistance = -1;
        CityKDTreeNode targetNode = new CityKDTreeNode();
        for (Map.Entry<CityKDTreeNode, Double> entry : entrySet) {
            double distance = Math.abs(entry.getValue() - closestTo);
            if (distance < lowestDistance || lowestDistance == -1) {
                lowestDistance = distance;
                targetNode = entry.getKey();
            }
        }
        return targetNode;
    }

    private CityKDTreeNode trimmingNode(CityKDTreeNode node, Rectangle r) {

        double nodeUpperLeftX = node.getRectangle().getUpperLeftX();
        double nodeUpperLeftY = node.getRectangle().getUpperLeftY();
        double nodeBottomRightX = node.getRectangle().getBottomRightX();
        double nodeBottomRightY = node.getRectangle().getBottomRightY();

        // first split: horizontal cut, if necessary
        // Round to 3 digits to prevent infinity loop, because e.g. 12.34000000007 is
        // declared equal to 12.34
        if (Math.round(node.getRectangle().getLength() * 1000d) != Math.round(r.getLength() * 1000d)) {
            // new child-nodes
            node.setLeftChild(new CityKDTreeNode(
                    new Rectangle(nodeUpperLeftX, nodeUpperLeftY, nodeBottomRightX, (nodeUpperLeftY + r.getLength()))));
            node.setRightChild(new CityKDTreeNode(new Rectangle(nodeUpperLeftX, (nodeUpperLeftY + r.getLength()),
                    nodeBottomRightX, nodeBottomRightY)));
            // set node as occupied (only leaves can contain elements)
            node.setOccupied();



            return trimmingNode(node.getLeftChild(), r);
            // second split: vertical cut, if necessary
            // Round to 3 digits, because e.g. 12.34000000007 is declared equal to 12.34
        } else if (Math.round(node.getRectangle().getWidth() * 1000d) != Math.round(r.getWidth() * 1000d)) {
            // new child-nodes
            node.setLeftChild(new CityKDTreeNode(
                    new Rectangle(nodeUpperLeftX, nodeUpperLeftY, (nodeUpperLeftX + r.getWidth()), nodeBottomRightY)));
            node.setRightChild(new CityKDTreeNode(new Rectangle((nodeUpperLeftX + r.getWidth()), nodeUpperLeftY,
                    nodeBottomRightX, nodeBottomRightY)));
            // set node as occupied (only leaves can contain elements)
            node.setOccupied();


            return node.getLeftChild();
        } else {

            return node;
        }
    }




    private void updateCovrec(CityKDTreeNode fitNode, Rectangle covrec) {
        double newX = (Math.max(fitNode.getRectangle().getBottomRightX(), covrec.getBottomRightX()));
        double newY = (Math.max(fitNode.getRectangle().getBottomRightY(), covrec.getBottomRightY()));
        covrec.changeRectangle(0, 0, newX, newY);
    }




}