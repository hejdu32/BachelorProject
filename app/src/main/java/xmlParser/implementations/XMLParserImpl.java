/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xmlParser.implementations;
import crosby.binary.osmosis.OsmosisReader;
import org.opengis.referencing.FactoryException;
import xmlParser.framework.XMLParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class XMLParserImpl implements XMLParser {
    private List<CustomWay> ways = new ArrayList<>();
    private Map<Long, CustomNode> nodes = new HashMap<>();
    private Set<Long> nodesToSearchFor = new HashSet<>();
    private DistanceCalculatorImpl distanceCalculator;

    @Override
    public void parse(String path) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("src/resources/denmark-latest.osm.pbf");
        OsmosisReader reader = new OsmosisReader(inputStream);
        try {
            this.distanceCalculator = new DistanceCalculatorImpl("EPSG:4326", "EPSG:25832");
            //First pass through data
            reader.setSink(new FirstPassSink(this));
            reader.run();
            //Second pass through data
            InputStream inputStream1 = new FileInputStream("src/resources/denmark-latest.osm.pbf");
            OsmosisReader reader1 = new OsmosisReader(inputStream1);
            reader1.setSink(new SecondPassSink(this));
            reader1.run();
        } catch (FactoryException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<CustomWay> getWays() {
        return ways;
    }

    @Override
    public Set<Long> getNodesToSearchFor() {
        return nodesToSearchFor;
    }

    @Override
    public Map<Long, CustomNode> getNodes() {
        return nodes;
    }

    @Override
    public DistanceCalculatorImpl getDistanceCalculator() {
        return distanceCalculator;
    }


}
