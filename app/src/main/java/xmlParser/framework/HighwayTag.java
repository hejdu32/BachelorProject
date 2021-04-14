package xmlParser.framework;

public enum HighwayTag {
    MOTORWAY ("motorway"), TRUNK ("trunk"), PRIMARY ("primary"),
    SECONDARY ("secondary"), TERTIARY ("tertiary"), UNCLASSIFIED ("unclassified"),
    RESIDENTIAL ("residential"), MOTORWAY_LINK ("motorway_link"), TRUNK_LINK ("trunk_link"),
    PRIMARY_LINK ("primary_link"), SECONDARY_LINK ("secondary_link"), TERTIARY_LINK ("tertiary_link"),
    LIVING_STREET ("living_street"), SERVICE ("service"), TRACK ("track"), TURNING_CIRCLE("turning_circle"), ROAD("road");

    private final String name;

    private HighwayTag(String s){
        this.name = s;
    }
    @Override
    public String toString(){
        return this.name;
    }
}
