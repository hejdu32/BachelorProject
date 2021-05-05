package xmlParser.implementations.parsing;

import com.google.gson.Gson;
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

    public HashMap<Long, List<Edge>> createAdjacencyList() throws TransformException {
        HashMap<Long, List<Edge>> adjencencyList = new HashMap<>();
        for(CustomWay way: xmlParser.getWays().values()){
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

                    double dist = distanceCalculator.calculateDistanceWithSpeed(prevCoord, currCoord, Integer.parseInt(way.getMaxSpeed()));

                    addEdgeToList(adjencencyList, previousId, currId, dist);

                    if(!way.isOneWay().equals("1")) {
                        addEdgeToList(adjencencyList, currId, previousId, dist);
                    }

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
            line = new StringBuilder("#" + node + " " + nodes.get(node).getLatitudeAsXCoord() + " " + nodes.get(node).getLongtitudeAsYCoord() + "\n");
            writer.write(line.toString());
        }

        for (CustomWay w:ways){
            line = new StringBuilder(";" + w.getMaxSpeed());
            for (Long nodeid:w.getNodeIdList()){
                line.append(" ").append(nodeid);
            }
            line.append("\n");
            writer.write(line.toString());
        }
        writer.write("!");
        writer.close();
    }

    public void writeWAdjList(String s, Map<Long, CustomNode> nodes, List<CustomWay> ways) throws IOException {
        int amountOfNodes = nodes.size();
        int amountOfWays = ways.size();
        StringBuilder line = new StringBuilder(amountOfNodes + "\n" + amountOfWays + "\n");
        FileWriter writer = new FileWriter(s);
        writer.write(line.toString());
        //choords
        for (long node:nodes.keySet()) {
            line = new StringBuilder(node + "\n" + nodes.get(node).getLatitudeAsXCoord() + "\n" + nodes.get(node).getLongtitudeAsYCoord() + "\n");
            writer.write(line.toString());
        }

        for (CustomWay w:ways){
            line = new StringBuilder(w.getMaxSpeed());
            line.append(" ").append(w.isOneWay());
            for (Long nodeid:w.getNodeIdList()){
                line.append(" ").append(nodeid);
            }
            line.append("\n");
            writer.write(line.toString());
        }
        writer.write("!");
        writer.close();

    }
    public void writeToFileAsJson(String filePath) throws IOException {
         Gson gson = new Gson();
         NodesAndWaysWrapper wrapper = new NodesAndWaysWrapper(14, xmlParser.getWays(), xmlParser.getNodes());
         String json = gson.toJson(wrapper);
         FileWriter writer = new FileWriter(filePath);
         writer.write(json);
         writer.close();

    }

    public HashMap<Long, List<Edge>> reduceAdjacencyList(HashMap<Long, List<Edge>> adjacencyList) {
        Map<Long, List<Long>> nodesToSearhFor = xmlParser.getNodesToSearchFor();
        for(Long id: nodesToSearhFor.keySet()){
            if(nodesToSearhFor.get(id).size() > 1) {
                // REMOVE EXCESS NODES FROM ADJACENCYLIST
            }
        }
        return null;
    }
}





















