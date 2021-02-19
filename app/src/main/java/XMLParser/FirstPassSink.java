package XMLParser;
import XMLParser.Implementations.DistanceCalculatorImpl;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;
public class FirstPassSink implements Sink{
    private double long1 = 0.0;
    private double lat1 = 0.0;
    private double long2 = 0.0;
    private double lat2 = 0.0;
    private DistanceCalculatorImpl distanceCalculator;

    public FirstPassSink(DistanceCalculatorImpl distanceCalculator){
        this.distanceCalculator = distanceCalculator;
    }
    @Override
    public void process(EntityContainer entityContainer) {


        if (entityContainer instanceof NodeContainer){
            Node node1 =((NodeContainer) entityContainer).getEntity();
            if(node1.getId() == 4924817521L){
                long1 = node1.getLongitude();
                lat1 = node1.getLatitude();
            }
            if(node1.getId() == 4926129286L){
                long2 = node1.getLongitude();
                lat2 = node1.getLatitude();
            }
        }
        if (entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            if (way.getId() == 501775256) {
                Coordinate coord1 = new Coordinate(lat1, long1);
                Coordinate coord2 = new Coordinate(lat2, long2);
                try {
                    System.out.println(distanceCalculator.calculateDistance(coord1, coord2));
                } catch (TransformException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void initialize(Map<String, Object> metaData) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }
}
