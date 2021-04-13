package xmlParser.framework;

import xmlParser.implementations.parsing.CustomNode;

import java.awt.*;
import java.util.Map;

public interface NodeFinder {
    long findClosestNodeToPoint(double x, double y, Map<Long, CustomNode> nodes, int xOffset, int yOffset, double scaleFactor);
    Point convertCoordsXYToImageXY(double x, double y, int xOffset, int yOffset, double scaleFactor);
}
