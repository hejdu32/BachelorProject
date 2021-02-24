package xmlParser.implementations;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import xmlParser.framework.HighwayTag;


import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class FirstPassSink implements Sink{

    private final XMLParser parser;
    private final DistanceCalculatorImpl distanceCalculator;

    public FirstPassSink(DistanceCalculatorImpl distanceCalculator, XMLParser parser){
        this.distanceCalculator = distanceCalculator;
        this.parser = parser;

    }
    @Override
    public void process(EntityContainer entityContainer) {

        if (entityContainer instanceof WayContainer){
            Way way = ((WayContainer) entityContainer).getEntity();
            Collection<Tag> tags = way.getTags();
            for(Tag tag: tags){
                if(tag.getKey().equals("highway")){
                    if(checkTag(tag.getValue())){
                        MyWay myWay = new MyWay(way.getId(),
                                                way.getWayNodes().stream().mapToLong(WayNode::getNodeId).boxed().collect(Collectors.toList()),
                                                tag.getValue());
                        parser.getNodesToSearchFor().addAll(myWay.getNodeIdList());
                        parser.getWays().add(myWay);
                    }
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

    private boolean checkTag(String tag) {
        HighwayTag[] highwayTags = HighwayTag.values();
        for(HighwayTag hTag: highwayTags) {
            if (hTag.toString().equals(tag)) {
               return true;
            }
        }
        return false;
    }
}
