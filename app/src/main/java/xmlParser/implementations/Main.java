package xmlParser.implementations;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.CoordinateCodes;
import xmlParser.framework.DistanceCalculator;
import xmlParser.framework.XMLParser;
import xmlParser.implementations.parsing.*;
import xmlParser.implementations.testImplementation.XMLParserStump;
import xmlParser.implementations.testImplementation.XMLVisualizationStump;
import xmlParser.implementations.util.DistanceCalculatorImpl;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, FactoryException, TransformException {

        XMLParserImpl parser = new XMLParserStump();
        GraphBuilder graphBuilder = new GraphBuilder(parser);

        var pb = new ProcessBuilder();
        pb.command("C:/proj/BachelorCpp/app/build/exe/test/appTest.exe");  // C++ executable
        var process = pb.start();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        //parser.parse("S");
        boolean reading = true;
        System.out.println("###########################################################################");
//        Thread readerThread = new Thread(() ->
//        {
//            while(true){
//                try {
//                    System.out.println(reader.readLine());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        readerThread.start();
        while(reading){
            String input = reader1.readLine();
            switch(input.toLowerCase()){
                case "exit":
                    process.destroy();
                    reading = false;
                    break;
                case "makelist":
                    HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjencencyList();
                    writer.write("makeAdjencencyList" + "\n");
                    writer.flush();
                    for (Long key:adjList.keySet()) {
                        String line = "#" + key;
                        for (Edge e : adjList.get(key)) {
                            line = line + " ;" + e.getDestinationId() + " ," + e.getDistanceToDestination();
                        }
                        //System.out.println(line);
                        writer.write(line + "\n");
                        writer.flush();
                    }
                    writer.write("!" + "\n" );
                    writer.flush();
                    System.out.println(reader.readLine());
                    break;
                case "rundijkstra":
                    System.out.println("Input from nodeId");
                    String from = reader1.readLine();
                    System.out.println("Input to nodeId");
                    String to = reader1.readLine();
                    writer.write("runDijkstra" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    System.out.println(reader.readLine());
                    break;
                default:
                    break;
            }
        }
    }


//       JFrame frame = new JFrame();
//       GraphOfNodes graphOfNodes = new GraphOfNodes((parser));
//       frame.getContentPane().add(graphOfNodes);
//
//       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       frame.setSize(1300,1300);
//       frame.setVisible(true);

}

