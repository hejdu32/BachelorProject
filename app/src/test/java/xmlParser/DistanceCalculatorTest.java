package xmlParser;
import xmlParser.framework.CoordinateCodes;
import xmlParser.framework.DistanceCalculator;
import xmlParser.implementations.util.DistanceCalculatorImpl;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class DistanceCalculatorTest {
    DistanceCalculator distanceCalculator;
    @Before
    public void setUp() {
        try {
            this.distanceCalculator = new DistanceCalculatorImpl(CoordinateCodes.STANDARD, CoordinateCodes.DENMARK);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void calculateKnownDistanceBetweenTwoNodes(){
        Coordinate coord1 = new Coordinate(56.6995893, 11.5423112);
        Coordinate coord2 = new Coordinate(56.6996669, 11.5417454);
        double distance = 0.0;
        try {
            distance = this.distanceCalculator.calculateDistance(coord1, coord2);
        } catch (TransformException e) {
            e.printStackTrace();
        }
        assertEquals(distance, 35.718652250837295, 0.00001);

    }
}
