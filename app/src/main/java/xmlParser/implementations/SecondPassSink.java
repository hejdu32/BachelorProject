package xmlParser.implementations;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class SecondPassSink implements Sink {

    private final XMLParser parser;

    public SecondPassSink(XMLParser parser){
        this.parser = parser;
    }
    @Override
    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer) {
            Node node =((NodeContainer) entityContainer).getEntity();
            long nodeId = node.getId();
            if(parser.getNodesToSearchFor().contains(nodeId)){
                Coordinate coord = new Coordinate(node.getLatitude(), node.getLongitude());
                try {
                    parser.getDistanceCalculator().coordTransform(coord);
                } catch (TransformException e) {
                    e.printStackTrace();
                }
                parser.getNodes().put(nodeId, new MyNode(nodeId, coord.x,coord.y));
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

    public XMLParser getParser() {
        return parser;
    }

}