/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xmlParser;

import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.parsing.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class XMLParserImplTest {
    private static final XMLParserImpl parser = new XMLParserImpl();
    private static boolean setUpIsDone = false;
    private static GraphBuilder graphBuilder;
    private static HashMap<Long, List<Edge>> adjList;
    {
        try {
            graphBuilder = new GraphBuilder(parser);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    //private static HashMap<Long, List<Edge>> adjList;
    //{
    //    try {
    //        System.out.println("Starting adjlistmaking");
    //        long startTime = System.currentTimeMillis();
    //        adjList = graphBuilder.createAdjencencyList();
    //        long endTime = System.currentTimeMillis();
    //        System.out.println("time for ajdlist creation: "+ (endTime- startTime)/1000 + "sec");
    //    } catch (TransformException e) {
    //        e.printStackTrace();
    //    }
    //}


    @Before
    public void setUp() {
        if(setUpIsDone){
            return;
        }
        try {
            parser.parse("src/resources/denmark-latest.osm.pbf");
            setUpIsDone = true;
            System.out.println("Starting adjlistmaking");
            long startTime = System.currentTimeMillis();
            adjList = graphBuilder.createAdjacencyList();
            long endTime = System.currentTimeMillis();
            System.out.println("time for ajdlist creation: "+ (endTime- startTime)/1000 + "sec");
        } catch (FileNotFoundException | TransformException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void checkForExistanceOfTwoKnownWays(){
            long id1 = 0;
            long id2 = 0;
            for(CustomWay myWay: parser.getWays().values()) {
                long id = myWay.getId();
                if(id == 279060626){
                    id1 = id;
                }
                if(id == 616476468){
                    id2 = id;
                }
            }
            assertEquals(279060626, id1);
            assertEquals(616476468, id2);
    }

    @Test
    public void checkIfNumberOfNodesInDatastructureIsCorrect(){
        assertEquals(parser.getNodes().size(), parser.getNodesToSearchFor().size());
    }

    @Test
    public void checkSanityOfCoordinateTransform(){
        CustomNode node1 = parser.getNodes().get(4939299713L);
        CustomNode node2 = parser.getNodes().get(8190430016L);
        assertNotEquals(0, node1.getLatitudeAsXCoord());
        assertNotEquals(0, node1.getLongtitudeAsYCoord());
        assertNotEquals(0, node2.getLatitudeAsXCoord());
        assertNotEquals(0, node2.getLongtitudeAsYCoord());
    }

    @Test
    public void checkTwoWayNodesAreConnected(){
        assertEquals(1758336175L, adjList.get(1758336171L).get(0).getDestinationId());
        assertEquals(1758336171L, adjList.get(1758336175L).get(0).getDestinationId());
    }



    @Test
    public void checkIntersectionForMultipleConnections(){
        assertEquals(3, adjList.get(258379884L).size());
    }

    @Test
    public void checkThatAdjListListIsNotEmpty(){
        int size = 0;
        long longid = 0L;
        for (Long edgeListId:adjList.keySet()) {
            List<Edge> edgeList = adjList.get(edgeListId);

            if(edgeList.size()>size) {
                size = edgeList.size();
                longid = edgeListId;
            }

            assertNotEquals(0, edgeList.size());
        }
        System.out.println("The long one is: " + longid + " with size: " + size);
    }

    @Test
    public void calcKindsofWaysAndAmountOfIntersections() {
        HashMap<String, Integer> kindofWays = new HashMap<>();
        for (CustomWay w:parser.getWays().values()) {
            if (kindofWays.containsKey(w.getTagId())){
            kindofWays.put(w.getTagId(),kindofWays.get(w.getTagId())+1);
            } else {
                kindofWays.put(w.getTagId(),1);
            }
        }
        kindofWays.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    @Test
    public void makeDenmarkInFile(){
        try {
            long startTime = System.currentTimeMillis();
            //graphBuilder.writeToFile("denmark", new ArrayList<>(parser.getWays().values()), parser.getNodes(), adjList);
            graphBuilder.writeWAdjList("denmark", parser.getNodes(), new ArrayList<>(parser.getWays().values()));
            long endTime = System.currentTimeMillis();
            System.out.println("Time for writing to file: "+ (endTime- startTime)/1000 + "sec");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void makeMaltaInFile(){
        try {
            long startTime = System.currentTimeMillis();
            //graphBuilder.writeToFile("malta", new ArrayList<>(parser.getWays().values()), parser.getNodes(), adjList);
            graphBuilder.writeWAdjList("malta", parser.getNodes(), new ArrayList<>(parser.getWays().values()));
            long endTime = System.currentTimeMillis();
            System.out.println("Time for writing to file: "+ (endTime- startTime)/1000 + "sec");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void randomTest(){
        String xd = "030";
        int val = Integer.parseInt(xd);
        System.out.println(val);
        try {
            val = Integer.parseInt("0a");
        }catch (NumberFormatException ex)
        {
            System.out.println("cannot convert " +"0a" + " into string");
        }
    }
}
