package xmlParser;

import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.NodeFinder;
import xmlParser.framework.ViewLimiter;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.Edge;
import xmlParser.implementations.parsing.GraphBuilder;
import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.testImplementation.XMLParserStump;
import xmlParser.implementations.util.NodeFinderImpl;
import xmlParser.implementations.util.ViewLimiterImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class NodeFinderTest {
    private static final XMLParserImpl parser = new XMLParserStump();
    private static boolean setUpIsDone = false;
    private static GraphBuilder graphBuilder;

    {
        try {
            graphBuilder = new GraphBuilder(parser);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkClosestNodes(){
        HashMap<Long, List<Edge>> reducedList = new HashMap<>();
        reducedList.put(1L, Arrays.asList(new Edge(2L, 1)));
        reducedList.put(2L, Arrays.asList(new Edge(1L, 1)));
        reducedList.put(2L, Arrays.asList(new Edge(3L, 1)));
        reducedList.put(3L, Arrays.asList(new Edge(2L, 1)));
        reducedList.put(2L, Arrays.asList(new Edge(5L, 1)));
        reducedList.put(5L, Arrays.asList(new Edge(2L, 1)));
        NodeFinderImpl nodeFinder = new NodeFinderImpl();
        List<Long> result = nodeFinder.findClosestReducedNodes(4L, parser, reducedList);
        assertEquals(2, result.size());
    }
}