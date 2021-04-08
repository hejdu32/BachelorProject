package xmlParser.implementations.util;

import xmlParser.framework.NodeFinder;
import xmlParser.implementations.parsing.CustomNode;

import java.awt.*;
import java.util.Map;

public class NodeFinderImpl implements NodeFinder {
    @Override
    public long findClosestNodeToPoint(int x, int y, Map<Long, CustomNode> nodes, int xOffset, int yOffset, double scaleFactor) {
        long resultNode = 0;
        Iterable<CustomNode> ListOfNodes = nodes.values();
        double bestSoFar = Integer.MAX_VALUE;
        for (CustomNode node:ListOfNodes) {
            double actualX = node.getLatitudeAsXCoord();
            double actualY = node.getLongtitudeAsYCoord();

            double diffX = Math.abs(x-((actualX-xOffset)/scaleFactor));
            double diffY = Math.abs(y-((actualY-yOffset)/scaleFactor));
            if( diffX + diffY <= bestSoFar) {
                bestSoFar = diffX+diffY;
                resultNode = node.getId();
            }
        }
        return resultNode;
    }

    public Point convertCoordsXYToImageXY(int x, int y, int xOffset, int yOffset, double scaleFactor) {
        int imageX=0;
        int imageY=0;
        imageX = (int) ((x-xOffset)/scaleFactor);
        imageY = (int) ((y-yOffset)/scaleFactor);
        return new Point(imageX,imageY);
    }
}
