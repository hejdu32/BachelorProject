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
import java.util.ArrayList;
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
    //COUNTRY TO BE PARSED
    private static final String country = "malta";

    public static void main(String[] args) throws IOException, FactoryException {//TransformException

        Main listener = new Main();
        XMLParserImpl parser = new XMLParserImpl();
        pb = new ProcessBuilder();
        pb.command("C:/Users/svend/CLionProjects/BachelorCppRestructured/cmake-build-release/src/BachelorCppCmake.exe");  // C++ executable
        process = pb.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        parser.parse("app/src/resources/"+country+"-latest.osm.pbf");
        parser.filterFerryWays();

        GraphBuilder graphBuilder = new GraphBuilder(parser);

        File possibleCountryFile = new File(country);
        String pathToCppFile;
        if (!possibleCountryFile.exists()){
            graphBuilder.writeWAdjList(country,parser.getNodes(),new ArrayList<>(parser.getWays().values()));
            pathToCppFile= possibleCountryFile.getAbsolutePath().replaceAll("\\\\","/");
        } else {
            pathToCppFile = possibleCountryFile.getAbsolutePath().replaceAll("\\\\","/");
        }

        JFrame frame = new JFrame();

        graphOfNodes = new GraphOfNodes((parser));
        graphOfNodes.addPropertyChangeListener(listener);
        frame.getContentPane().add(graphOfNodes);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300,1300);
        frame.setVisible(true);

        ReaderThread rd = new ReaderThread(reader,graphOfNodes);
        rd.start();
        //rd.run(reader,graphOfNodes);

        boolean reading = true;
        System.out.println("###########################################################################");
        while(reading){
            String input = inputReader.readLine();
            String lineToSend;
            switch(input.toLowerCase()){
                case "exit":
                    process.destroy();
                    reading = false;
                    System.out.println("Exiting");
                    rd.interrupt();
                    System.exit(0);
                case "makelist":
                    //HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
                    writer.write("makeAdjacencyList " + pathToCppFile + "\n");
                    writer.flush();
                    break;
                case "rundijkstra":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    lineToSend = "runDijkstra"+" " + from + " "+  to +  "\n";
                    //System.out.println(lineToSend);
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(reader.readLine(), Color.red); //also draws route
                    break;
                case "runastar":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    lineToSend = "runAstar"+" " + from + " "+  to +  "\n";
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(reader.readLine(), Color.green); //also draws route
                    break;
                case "runalt":
                    //System.out.println("Input from nodeId");
                    from = "1818942364";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5543870050";//reader1.readLine();
                    lineToSend = "runALT"+" " + from + " "+  to + "\n";
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(reader.readLine(), Color.green); //also draws route
                    break;
                case "testd":
                    System.out.println("test started rundijkstra");
                    from = graphOfNodes.getFrom();
                    System.out.println("From: " + from);
                    to = graphOfNodes.getTo();
                    System.out.println("To: " + to);
                    lineToSend = "runDijkstra"+" " + from + " "+  to + "\n";
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(result, Color.red); //also draws route
                    break;
                case "testa":
                    System.out.println("test started runAstar");
                    from = graphOfNodes.getFrom();
                    System.out.println("From: " + from);
                    to = graphOfNodes.getTo();
                    System.out.println("To: " + to);
                    lineToSend = "runAstar"+" " + from + " "+  to + "\n";
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
                    break;
                case "reset":
                    graphOfNodes.setImageX(0);
                    graphOfNodes.setImageY(0);
                    try {
                        HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
                        //System.out.println("nodesToSearch size: " + parser.getNodesToSearchFor().keySet().size());
                        System.out.println("adjList size: " + adjList.keySet().size());
                        graphOfNodes.setAdjacencyList(graphBuilder.reduceAdjacencyList(adjList));
                    } catch (TransformException e) {
                        e.printStackTrace();
                    }
                    graphOfNodes.repaint();
                    break;
                default:
                    System.out.println("malformed input: " +input);
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
                String lineToSend = "runAstar"+" " + from + " "+  to + "\n";
                writer.write(lineToSend);
                writer.flush();
                //result = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
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

