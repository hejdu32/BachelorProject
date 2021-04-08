package xmlParser.framework;

import xmlParser.implementations.parsing.CustomWay;

import java.util.List;

public interface ViewLimiter {
    List<CustomWay> limitToRelevantWays(double sourceX, double sourceY, double destX, double destY, int xOffset, int yOffset, double scaleFactor);
    void setMargin();
}
