package xmlParser;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import xmlParser.implementations.DistanceCalculatorImpl;


import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import xmlParser.implementations.MyWay;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FirstPassSink implements Sink{

    private final XMLParser parser;
    private Map<String, Node> nodeMap;
    private final DistanceCalculatorImpl distanceCalculator;

    public FirstPassSink(DistanceCalculatorImpl distanceCalculator, XMLParser parser){
        this.distanceCalculator = distanceCalculator;
        this.parser = parser;

    }
    @Override
    public void process(EntityContainer entityContainer) {

        if (entityContainer instanceof NodeContainer){
            Node node1 =((NodeContainer) entityContainer).getEntity();
        }
        if (entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            MyWay myWay = new MyWay(way.getId(),
                                    way.getWayNodes().stream().mapToLong(WayNode::getNodeId).boxed().collect(Collectors.toList()),
                                    "placeholder");
            parser.getWays().add(myWay);


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
