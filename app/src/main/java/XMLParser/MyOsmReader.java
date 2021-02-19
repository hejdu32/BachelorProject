package XMLParser;
import crosby.binary.osmosis.OsmosisReader;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.awt.*;
import java.util.Map;
public class MyOsmReader implements Sink{
    private double long1 = 0.0;
    private double lat1 = 0.0;
    private double long2 = 0.0;
    private double lat2 = 0.0;
    @Override
    public void process(EntityContainer entityContainer) {


        if (entityContainer instanceof NodeContainer){
            Node node1 =((NodeContainer) entityContainer).getEntity();
            if(node1.getId() == 4924817521L){
                System.out.println(node1.getLatitude());
                System.out.println(node1.getLongitude());
                long1 = node1.getLongitude();
                lat1 = node1.getLatitude();
            }
            if(node1.getId() == 4926129286L){
                System.out.println(node1.getLatitude());
                System.out.println(node1.getLongitude());
                long2 = node1.getLongitude();
                lat2 = node1.getLatitude();
            }
        }
        if (entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            if (way.getId() == 501775256) {
                try {
                    CoordinateReferenceSystem crsSource = CRS.decode("EPSG:4326");
                    CoordinateReferenceSystem crsTarget = CRS.decode("EPSG:25832");
                    MathTransform mathTransform = CRS.findMathTransform(crsSource, crsTarget);
                    Coordinate coord1 = new Coordinate(lat1, long1);
                    Coordinate coord2 = new Coordinate(lat2, long2);
                    System.out.println(coord1.x);
                    JTS.transform(coord1, coord1, mathTransform);
                    JTS.transform(coord2, coord2, mathTransform);
                    System.out.println(coord1.x);
                    double x1 = coord1.getX();
                    double x2 = coord2.getX();
                    double y1 = coord1.getY();
                    double y2 = coord2.getY();
                    System.out.println(x1 + " " + x2 + " "+ y1 + " "+ y2);
                    double distance = Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
                    System.out.println(distance + " første");
                    double distance2 = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
                    System.out.println(distance2 + " anden");

                } catch (FactoryException | TransformException e) {
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
