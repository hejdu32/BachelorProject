package xmlParser.implementations.util;

import xmlParser.framework.NodeFinder;
import xmlParser.implementations.parsing.CustomNode;

import java.awt.*;
import java.util.Map;

public class NodeFinderImpl implements NodeFinder {
    @Override
    public long findClosestNodeToPoint(double x, double y, Map<Long, CustomNode> nodes, int xOffset, int yOffset, double scaleFactor) {
        long resultNode = 0;
        double actualX = 0;
        double actualY = 0;
        Iterable<CustomNode> ListOfNodes = nodes.values();
        double bestSoFar = Integer.MAX_VALUE;
        for (CustomNode node:ListOfNodes) {
            actualX = node.getLatitudeAsXCoord();
            actualY = node.getLongtitudeAsYCoord();

            double diffX = Math.abs(x-((actualX-xOffset)/scaleFactor));
            double diffY = Math.abs(Math.abs(y-1300*8)-((actualY-yOffset)/scaleFactor));
            if( diffX + diffY <= bestSoFar) {
                bestSoFar = diffX+diffY;

                resultNode = node.getId();
            }
        }
        System.out.println(resultNode + " @ " + nodes.get(resultNode).getLatitudeAsXCoord() + ", " + nodes.get(resultNode).getLongtitudeAsYCoord()); //convertCoordsXYToImageXY(, , xOffset, yOffset, scaleFactor));
        return resultNode;
    }

    public Point convertCoordsXYToImageXY(double x, double y, int xOffset, int yOffset, double scaleFactor) {
        int imageX=0;
        int imageY=0;
        imageX = (int) ((x-xOffset)/scaleFactor);
        imageY = (int) ((y-yOffset)/scaleFactor);
        return new Point(imageX,imageY);
    }
}
