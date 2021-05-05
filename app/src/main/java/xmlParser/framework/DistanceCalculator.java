package xmlParser.framework;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.TransformException;

public interface DistanceCalculator {

    public double calculateDistance(Coordinate coord1, Coordinate coord2) throws TransformException;

    public double calculateDistanceWithSpeed(Coordinate coord1, Coordinate coord2, int maxSpeed) throws TransformException;
}
