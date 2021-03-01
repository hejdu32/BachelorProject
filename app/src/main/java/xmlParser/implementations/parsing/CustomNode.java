package xmlParser.implementations.parsing;

public class CustomNode {

    private final long id;
    private final double longtitudeAsYCoord;
    private final double latitudeAsXCoord;

    public CustomNode(long id, double latitudeAsXCoord, double longtitudeAsYCoord){
        this.id = id;
        this.longtitudeAsYCoord = longtitudeAsYCoord;
        this.latitudeAsXCoord = latitudeAsXCoord;
    }

    public long getId() {
        return id;
    }

    public double getLongtitudeAsYCoord() {
        return longtitudeAsYCoord;
    }

    public double getLatitudeAsXCoord() {
        return latitudeAsXCoord;
    }
}
