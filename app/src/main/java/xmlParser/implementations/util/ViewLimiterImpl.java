package xmlParser.implementations.util;

import xmlParser.framework.NodeFinder;
import xmlParser.framework.ViewLimiter;
import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewLimiterImpl implements ViewLimiter {
    private List<CustomWay> ways;
    private Map<Long, CustomNode> nodes;
    private int routeFactor;

    public ViewLimiterImpl(List<CustomWay> ways, Map<Long, CustomNode> nodes, int routeFactor) {
        this.ways = ways;
        this.nodes = nodes;
        this.routeFactor = routeFactor;
    }

    private double lowestX = Integer.MAX_VALUE, lowestY = Integer.MAX_VALUE, highestX = Integer.MIN_VALUE, highestY = Integer.MIN_VALUE;

    double marginX;
    double marginY;

    public List<CustomWay> limitToRelevantWays(long source, long dest, int xOffset, int yOffset, double scaleFactor) {
        double sourceX = nodes.get(source).getLatitudeAsXCoord();
        double sourceY = nodes.get(source).getLongtitudeAsYCoord();

        double destX = nodes.get(dest).getLatitudeAsXCoord();
        double destY = nodes.get(dest).getLongtitudeAsYCoord();

        return limitToRelevantWays(sourceX, sourceY, destX, destY, xOffset, yOffset, scaleFactor);
    }

    @Override
    public List<CustomWay> limitToRelevantWays(double sourceX, double sourceY, double destX, double destY, int xOffset, int yOffset, double scaleFactor) {
        //if there are no margin data, get margin data
        if (lowestX == Integer.MAX_VALUE) {
            setMargin();
        }

        List<CustomWay> result = new ArrayList<>();
        for (CustomWay way: ways) {
            NodeFinder nodeFinder = new NodeFinderImpl();
            boolean first = getNodeBound(sourceX, sourceY, destX, destY, xOffset, yOffset, scaleFactor, way, nodeFinder, 0);

            //get last node to represent way
            boolean last = getNodeBound(sourceX, sourceY, destX, destY, xOffset, yOffset, scaleFactor, way, nodeFinder, way.getNodeIdList().size() - 1);

            if (first || last) result.add(way);

        }
        return result;
    }

    private boolean getNodeBound(double sourceX, double sourceY, double destX, double destY, int xOffset, int yOffset, double scaleFactor, CustomWay way, NodeFinder nodeFinder, int i) {
        //get first node to represent way
        Long node = way.getNodeIdList().get(i);
        //get way x and y for inclusion calculation
        double latitudeAsXCoord = nodes.get(node).getLatitudeAsXCoord();
        double longtitudeAsYCoord = nodes.get(node).getLongtitudeAsYCoord();
        //find node close to x,y
        Point2D.Double wayPoint = nodeFinder.convertCoordsXYToImageXY((int) latitudeAsXCoord, (int) longtitudeAsYCoord, xOffset, yOffset, scaleFactor);
        //getting x and y
        double wayX = wayPoint.x;
        double wayY = wayPoint.y; //1300 -> 0,  650 -> 650,  0 -> 1300
        double flippedY = Math.abs(wayY - 1300*routeFactor); //magic constant for now

        boolean insideX = wayX - marginX <= Math.max(sourceX, destX) & wayX + marginX >= Math.min(sourceX, destX);
        //                            49 <= enten 50 eller 0        eller           49 >= enten 50 eller 0

        boolean insideY = flippedY - marginY <= Math.max(sourceY, destY) & flippedY + marginY >= Math.min(sourceY, destY);
        //                            49 <= enten 50 eller 0        eller           49 >= enten 50 eller 0
        return insideX & insideY;
    }

    @Override
    public void setMargin() {
        for (CustomWay way: ways) {
            for (Long node : way.getNodeIdList()) {
                //get way x and y for total x y difference
                if(nodes.get(node)==null){
                    System.out.println(way.getId());
                    System.out.println(way.getNodeIdList());
                    System.out.println("THE NODE: " + node);
                }
                double wayX = nodes.get(node).getLatitudeAsXCoord();
                double wayY = nodes.get(node).getLongtitudeAsYCoord();

                if (wayX < lowestX) lowestX = wayX;
                if (wayY < lowestY) lowestY = wayY;
                if (wayX > highestX) highestX = wayX;
                if (wayY > highestY) highestY = wayY;
            }
        }
        marginX = (highestX - lowestX) / 1;
        marginY = (highestY - lowestY) / 1;
    }

    public void setMargin(double marginX, double marginY) {
        this.marginX = marginX;
        this.marginY = marginY;
    }

    public double calculateScale(int fullResolution){
        //use offsets to calculate scale
        double xOffsetDiff = getHighestX() - getLowestX();
        double yOffsetDiff = getHighestY() - getLowestY();
        double diffToUse = Math.max((xOffsetDiff), (yOffsetDiff));
        return (diffToUse/fullResolution); //makes sure it fits inside the window
    }

    public double getLowestX() {
        return lowestX;
    }

    public void setLowestX(double lowestX) {
        this.lowestX = lowestX;
    }

    public double getLowestY() {
        return lowestY;
    }

    public void setLowestY(double lowestY) {
        this.lowestY = lowestY;
    }

    public double getHighestX() {
        return highestX;
    }

    public void setHighestX(double highestX) {
        this.highestX = highestX;
    }

    public double getHighestY() {
        return highestY;
    }

    public void setHighestY(double highestY) {
        this.highestY = highestY;
    }

}
