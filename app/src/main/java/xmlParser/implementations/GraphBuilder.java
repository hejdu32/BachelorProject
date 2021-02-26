package xmlParser.implementations;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.CoordinateCodes;
import xmlParser.framework.DistanceCalculator;
import xmlParser.framework.XMLParser;

import java.util.*;

public class GraphBuilder {

    private XMLParser xmlParser;
    private DistanceCalculator distanceCalculator;

    public GraphBuilder(XMLParser xmlParser) throws FactoryException {
        this.xmlParser = xmlParser;
        distanceCalculator = new DistanceCalculatorImpl(CoordinateCodes.STANDARD, CoordinateCodes.DENMARK);
    }

    public HashMap<Long, List<Edge>> createAdjencencyList() throws TransformException {
        HashMap<Long, List<Edge>> adjencencyList = new HashMap<>();
        for(CustomWay way: xmlParser.getWays()){
            long previousId = 0L;
            Iterator iterator = way.getNodeIdList().iterator();

            do {
                long currId = (long) iterator.next();
                if(previousId == 0L) {
                    previousId = currId;
                }
                else {
                    if(previousId==currId) System.out.println("CurrId is the same as previousId. This is an Error");

                    CustomNode previousNode = xmlParser.getNodes().get(previousId);
                    double prevX = previousNode.getLatitudeAsXCoord();
                    double prevY = previousNode.getLongtitudeAsYCoord();

                    CustomNode currNode = xmlParser.getNodes().get(currId);
                    double currX = currNode.getLatitudeAsXCoord();
                    double currY = currNode.getLongtitudeAsYCoord();

                    Coordinate prevCoord = new Coordinate(prevX, prevY);
                    Coordinate currCoord = new Coordinate(currX, currY);

                    double dist = distanceCalculator.calculateDistance(prevCoord, currCoord);

                    addEdgeToList(adjencencyList, previousId, currId, dist);

                    addEdgeToList(adjencencyList, currId, previousId, dist);

                    previousId = currId;
                }
            } while(iterator.hasNext());
        }

        return adjencencyList;
    }

    private void addEdgeToList(HashMap<Long, List<Edge>> adjencencyList, long from, long to, double dist) {
        List<Edge> edgeList = new ArrayList<>();
        Edge newEdge = new Edge(to, dist);
        if (adjencencyList.containsKey(from)) {
            edgeList = adjencencyList.get(from);
        }
        edgeList.add(newEdge);

        adjencencyList.put(from, edgeList);
    }


}
