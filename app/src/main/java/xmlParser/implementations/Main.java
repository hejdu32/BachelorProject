package xmlParser.implementations;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.parsing.*;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Main implements PropertyChangeListener {



    private static String fromNode;
    private static String toNode;
    private static String result;
    private static String from;
    private static String to;
    private static GraphOfNodes graphOfNodes;
    private static ProcessBuilder pb;
    private static Process process;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException, FactoryException, TransformException  {

        Main listener = new Main();
        XMLParserImpl parser = new XMLParserImpl();
        //XMLParserImpl parser = new XMLParserStump();
        GraphBuilder graphBuilder = new GraphBuilder(parser);

        pb = new ProcessBuilder();
        pb.command("C:/Users/svend/CLionProjects/BachelorCppRestructured/cmake-build-debug/src/BachelorCppCmake.exe");  // C++ executable
        process = pb.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        parser.parse("app/src/resources/malta-latest.osm.pbf");

        JFrame frame = new JFrame();

        graphOfNodes = new GraphOfNodes((parser));
        graphOfNodes.addPropertyChangeListener(listener);
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
                    //for (Long key:adjList.keySet()) {
                    //    String line = "#" + key;
                    //    for (Edge e : adjList.get(key)) {
                    //        line = line + " ;" + e.getDestinationId() + " ," + e.getDistanceToDestination();
                    //    }
                    //    writer.write(line + "\n");
                    //    writer.flush();
                    //}
                    //writer.write("!" + "\n" );
                    //writer.flush();
                    System.out.println(reader.readLine());
                    break;
                case "rundijkstra":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    writer.write("runDijkstra" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    graphOfNodes.setRouteToDraw(reader.readLine(), Color.red); //also draws route
                    break;
                case "runastar":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    writer.write("runAstar" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    graphOfNodes.setRouteToDraw(reader.readLine(), Color.green); //also draws route
                    break;
                case "testd":
                    System.out.println("test started rundijkstra");
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
                    graphOfNodes.setRouteToDraw(result, Color.red); //also draws route
                    break;
                case "testa":
                    System.out.println("test started runAstar");
                    from = graphOfNodes.getFrom();
                    System.out.println("From: " + from);
                    to = graphOfNodes.getTo();
                    System.out.println("To: " + to);
                    writer.write("runAstar" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    result = reader.readLine();
                    graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
                    break;
                case "reset":
                    graphOfNodes.setImageX(0);
                    graphOfNodes.setImageY(0);
                    graphOfNodes.repaint();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("SecoundClick")) {
            System.out.println("test started runAstar");
            from = graphOfNodes.getFrom();
            System.out.println("From: " + from);
            to = graphOfNodes.getTo();
            System.out.println("To: " + to);
            try {
                writer.write("runAstar" + "\n");
                writer.flush();
                writer.write(from + "\n");
                writer.flush();
                writer.write(to + "\n");
                writer.flush();
                result = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
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

