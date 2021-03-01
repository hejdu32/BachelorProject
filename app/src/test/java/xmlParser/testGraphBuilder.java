package xmlParser;

import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.parsing.Edge;
import xmlParser.implementations.parsing.GraphBuilder;
import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.testImplementation.XMLParserStump;

import java.util.HashMap;
import java.util.List;

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
            HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjencencyList();
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
            adjList = graphBuilder.createAdjencencyList();
        } catch (TransformException e) {
            e.printStackTrace();
        }
        assertEquals(3, adjList.get(2L).size());
    }
}
