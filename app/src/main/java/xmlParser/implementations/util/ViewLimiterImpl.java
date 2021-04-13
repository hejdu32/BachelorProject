package xmlParser.implementations.util;

import xmlParser.framework.NodeFinder;
import xmlParser.framework.ViewLimiter;
import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewLimiterImpl implements ViewLimiter {
    private List<CustomWay> ways;
    private Map<Long, CustomNode> nodes;

    public ViewLimiterImpl(List<CustomWay> ways, Map<Long, CustomNode> nodes) {
        this.ways = ways;
        this.nodes = nodes;
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
            //get first node to represent way
            Long node = way.getNodeIdList().get(0);

            //get way x and y for inclusion calculation
            double latitudeAsXCoord = nodes.get(node).getLatitudeAsXCoord();
            double longtitudeAsYCoord = nodes.get(node).getLongtitudeAsYCoord();

            //find node close to x,y
            NodeFinder nodeFinder = new NodeFinderImpl();
            Point wayPoint = nodeFinder.convertCoordsXYToImageXY( (int)latitudeAsXCoord, (int)longtitudeAsYCoord,xOffset,yOffset,scaleFactor);

            int wayX = wayPoint.x;
            int wayY = wayPoint.y; //1300 -> 0,  650 -> 650,  0 -> 1300
            int flippedY = Math.abs(wayY-1300*8); //magic constant for now

            boolean insideX = wayX - marginX <= Math.max(sourceX, destX) & wayX + marginX >= Math.min(sourceX, destX);
            //                            49 <= enten 50 eller 0        eller           49 >= enten 50 eller 0

            boolean insideY = flippedY - marginY <= Math.max(sourceY, destY) & flippedY + marginY >= Math.min(sourceY, destY);
            //                            49 <= enten 50 eller 0        eller           49 >= enten 50 eller 0
            if (insideX & insideY) result.add(way);

        }
        return result;
    }

    @Override
    public void setMargin() {
        for (CustomWay way: ways) {
            for (Long node : way.getNodeIdList()) {
                //get way x and y for total x y difference
                double wayX = nodes.get(node).getLatitudeAsXCoord();
                double wayY = nodes.get(node).getLongtitudeAsYCoord();

                if (wayX < lowestX) lowestX = wayX;
                if (wayY < lowestY) lowestY = wayY;
                if (wayX > highestX) highestX = wayX;
                if (wayY > highestY) highestY = wayY;
            }
        }
        marginX = (highestX - lowestX) / 10;
        marginY = (highestY - lowestY) / 10;
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
