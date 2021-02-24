package xmlParser.implementations;

public class MyNode {

    private final long id;
    private final double longtitude;
    private final double latitude;

    public MyNode(long id, double longtitude, double latitude){
        this.id = id;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }

    public long getId() {
        return id;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
