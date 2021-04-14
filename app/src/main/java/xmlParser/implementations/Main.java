package xmlParser.implementations;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.parsing.*;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Main {



    private static String fromNode;
    private static String toNode;
    private static String result;

    public static void main(String[] args) throws IOException, FactoryException, TransformException {

        XMLParserImpl parser = new XMLParserImpl();
        //XMLParserImpl parser = new XMLParserStump();
        GraphBuilder graphBuilder = new GraphBuilder(parser);

        var pb = new ProcessBuilder();
        pb.command("C:/Users/svend/CLionProjects/BachelorCpp2/app/build/exe/test/appTest.exe");  // C++ executable
        var process = pb.start();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        parser.parse("app/src/resources/malta-latest.osm.pbf");

        JFrame frame = new JFrame();
        GraphOfNodes graphOfNodes = new GraphOfNodes((parser));
        frame.getContentPane().add(graphOfNodes);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300,1300);
        frame.setVisible(true);

        boolean reading = true;
        System.out.println("###########################################################################");
        while(reading){
            String input = reader1.readLine();
            switch(input.toLowerCase()){
                case "exit":
                    process.destroy();
                    reading = false;
                    System.out.println("Exitting");
                    System.exit(0);
                case "makelist":
                    HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
                    writer.write("makeAdjacencyList" + "\n");
                    writer.flush();
                    for (Long key:adjList.keySet()) {
                        String line = "#" + key;
                        for (Edge e : adjList.get(key)) {
                            line = line + " ;" + e.getDestinationId() + " ," + e.getDistanceToDestination();
                        }
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
                    result = reader.readLine();
                    graphOfNodes.setRedPart(result);
                    break;
                case "testg":
                    System.out.println("test started");
                    from = graphOfNodes.getFrom();
                    System.out.println("From: " + from);
                    to = graphOfNodes.getTo();
                    System.out.println("To: " + to);
                    writer.write("runDijkstra" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    result = reader.readLine();
                    graphOfNodes.setRedPart(result);
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

