package xmlParser.implementations;

public class Edge {


    private final long destinationId;
    private final double distanceToDestination;

    public Edge(Long destinationId, double distanceToDestination){
        this.destinationId = destinationId;
        this.distanceToDestination = distanceToDestination;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public double getDistanceToDestination() {
        return distanceToDestination;
    }

}
