package xmlParser.implementations.util;

import org.opengis.referencing.FactoryException;
import xmlParser.implementations.parsing.GraphBuilder;
import xmlParser.implementations.parsing.XMLParserImpl;
import java.io.IOException;
import java.util.ArrayList;

public class FileWriter {
    private final XMLParserImpl parser = new XMLParserImpl();

    public void parseCountryToFile(String country){
        try {
            GraphBuilder graphBuilder = new GraphBuilder(parser);
            System.out.println("Parsing "+country);
            parser.parse("src/resources/"+country+"-latest.osm.pbf");
            System.out.println("Filtering ferry ways");
            parser.filterFerryWays();
            System.out.println("Creating AdjacencyList");
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();
            System.out.println("Time for adjlist creation: "+ (endTime- startTime)/1000 + "sec");
            long fileStart = System.currentTimeMillis();
            graphBuilder.writeAllWays(country, parser.getNodes(), new ArrayList<>(parser.getWays().values()));
            long fileEnd = System.currentTimeMillis();
            System.out.println("Time for writing to file: "+ (fileEnd- fileStart)/1000 + "sec");
        } catch (IOException | FactoryException e) {//TransformException |
            e.printStackTrace();
        }
    }


}
