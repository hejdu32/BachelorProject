package xmlParser.implementations.parsing;
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

    private final XMLParserImpl parser;

    public FirstPassSink(XMLParserImpl parser){
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
                        CustomWay customWay = new CustomWay(way.getId(),
                                                way.getWayNodes().stream().mapToLong(WayNode::getNodeId).boxed().collect(Collectors.toList()),
                                                tag.getValue(),typeToDefaultSpeed(tag.getValue()));
                        parser.getNodesToSearchFor().addAll(customWay.getNodeIdList());
                        parser.getWays().put(way.getId(), customWay);
                    }

                }
                if (tag.getKey().equals("maxspeed")){
                    boolean tagIsIntVal = isInValue(tag.getValue());
                    //if (!tagIsIntVal) {
                    //    System.out.println("Way: " + way.getId() + " maxspeed: " + tag.getValue());
                    //}
                    if (parser.getWays().containsKey(way.getId())&& tagIsIntVal){
                        //Integer.parseInt(tag.getValue())
                    parser.getWays().get(way.getId()).setMaxSpeed(tag.getValue());}
                }
            }


        }

    }

    private boolean isInValue(String value) {
        try{
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException ex){
            return false;
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
    public String typeToDefaultSpeed(String tag){
        HighwayTag enumtag = HighwayTag.valueOf(tag.toUpperCase());
        switch (enumtag){
            case MOTORWAY :
                return "130";
            case TRUNK :
            case PRIMARY :
            case SECONDARY :
            case TERTIARY :
            case MOTORWAY_LINK :
            case TRUNK_LINK :
            case PRIMARY_LINK :
            case SECONDARY_LINK :
            case TERTIARY_LINK :
                return "80";
            case UNCLASSIFIED :
            case RESIDENTIAL :
            case TRACK :
            case ROAD :
                return "50";
            default:
                return "20";
        }
    }
}


