package xmlParser.implementations.parsing;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.CoordinateCodes;
import xmlParser.framework.DistanceCalculator;
import xmlParser.framework.XMLParser;
import xmlParser.implementations.util.DistanceCalculatorImpl;

import java.io.FileWriter;
import java.io.IOException;
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


    //file convention is: #nodeid ;destid ,distance
    // a long relation would be #nodeid ;destid ,distance ;destid ,distance ;destid ,distance ;destid ,distance
    public void writeToFileOLD(String s, HashMap<Long, List<Edge>> adjLst){
        try {
            FileWriter writer = new FileWriter(s);
            for (Long key:adjLst.keySet()) {
                String line = "#"+key;
                for (Edge e:adjLst.get(key)) {
                    line = line+" ;"+e.getDestinationId()+" ,"+e.getDistanceToDestination();
                }
                writer.write(line+"\n");
            }
            writer.write("!");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //%mængde af noder ?mængde af veje
    //*1 ^4.21 ,3.21
    //*2 ^1.23 ,8.12
    //...
    //#1 ;2 ;3 ;4
    //.....
    //!
    public void writeToFile(String s, List<CustomWay> ways, Map<Long, CustomNode> nodes, HashMap<Long, List<Edge>> adjLst) throws IOException {
        int amountOfNodes = nodes.size();
        int amountOfWays = ways.size();
        StringBuilder line = new StringBuilder("%" + amountOfNodes + " ?" + amountOfWays + "\n");
        FileWriter writer = new FileWriter(s);
        writer.write(line.toString());
        //choords
        for (long node:nodes.keySet()) {
            line = new StringBuilder("#" + node + " ^" + nodes.get(node).getLatitudeAsXCoord() + " ," + nodes.get(node).getLongtitudeAsYCoord() + "\n");
            writer.write(line.toString());
        }
        //ways
        //for(long key:adjLst.keySet()){
        //    line = "#"+key;
        //    for(Edge e:adjLst.get(key)){
        //        line = line + " ;"+e.getDestinationId();
        //    }
        //    writer.write(line +"\n");
        //}
        for (CustomWay w:ways){
            line = new StringBuilder(";" + w.getTagId());
            for (Long nodeid:w.getNodeIdList()){
                line.append(" ").append(nodeid);
            }
            line.append("\n");
            writer.write(line.toString());
        }
        writer.write("!");
        writer.close();
    }


}





















