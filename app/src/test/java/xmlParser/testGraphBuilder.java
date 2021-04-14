package xmlParser;

import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.parsing.Edge;
import xmlParser.implementations.parsing.GraphBuilder;
import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.testImplementation.XMLParserStump;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class testGraphBuilder {
    private static final XMLParserImpl parser = new XMLParserStump();
    private GraphBuilder graphBuilder;

    @Before
    public void setUp() {
        try {
            graphBuilder = new GraphBuilder(parser);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkDistNonZero() {
        try {
            HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
            assertEquals(2L,adjList.get(1L).get(0).getDestinationId());
            assertNotEquals(adjList.get(1L).get(0).getDistanceToDestination(), 0.0);
            assertEquals(adjList.get(5L).get(0).getDistanceToDestination(), 10.0, 0.001);
        } catch (TransformException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkForIntersection() {
        HashMap<Long, List<Edge>> adjList = null;
        try {
            adjList = graphBuilder.createAdjacencyList();
        } catch (TransformException e) {
            e.printStackTrace();
        }
        assertEquals(3, adjList.get(2L).size());
    }

    @Test
    public void checkStumpFileCorrectFormat(){
        try {
            HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
            graphBuilder.writeToFileOLD("adjlist", adjList);

            String fileData = readFile("adjlist");
            String expectedOut = "#1 ;2 ,1.0" +
                    "#2 ;1 ,1.0 ;3 ,1.0 ;4 ,1.0" +
                    "#3 ;2 ,1.0" +
                    "#4 ;2 ,1.0 ;5 ,10.0" +
                    "#5 ;4 ,10.0" +
                    "!";
            assertEquals(expectedOut,fileData);
        } catch (TransformException e) {
            e.printStackTrace();
        }

    }


    private String readFile(String fileToRead){
        StringBuilder data = new StringBuilder();
        try {
            FileReader reader = new FileReader(fileToRead);
            Scanner myReader = new Scanner(reader);

            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
    @Test
    public void testWriteToFileAsJson() throws IOException {
        String filePath = "";
        graphBuilder.writeToFileAsJson(filePath);
    }
}
