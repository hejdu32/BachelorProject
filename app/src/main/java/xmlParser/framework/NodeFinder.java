package xmlParser.framework;

import xmlParser.implementations.parsing.CustomNode;

import java.awt.*;
import java.util.Map;

public interface NodeFinder {
    long findClosestNodeToPoint(int x, int y, Map<Long, CustomNode> nodes, int xOffset, int yOffset, double scaleFactor);
    Point convertCoordsXYToImageXY(int x, int y, int xOffset, int yOffset, double scaleFactor);
}
