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
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ViewLimiterTest {
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
    public void checkLimitMethod(){
        ViewLimiter viewLimiter = new ViewLimiterImpl(new ArrayList<>(parser.getWays().values()), parser.getNodes());
        assertEquals(2, parser.getWays().size());
        List<CustomWay> testways = viewLimiter.limitToRelevantWays(2, 2, 5, 5, 0, 0, 1);
        assertEquals(1, testways.size());
    }
}
