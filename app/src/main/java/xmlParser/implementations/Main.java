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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    public static void main(String[] args) throws IOException, FactoryException  {//TransformException

        Main listener = new Main();
        XMLParserImpl parser = new XMLParserImpl();
        GraphBuilder graphBuilder = new GraphBuilder(parser);

        pb = new ProcessBuilder();
        pb.command("C:/Users/svend/CLionProjects/BachelorCppRestructured/cmake-build-release/src/BachelorCppCmake.exe");  // C++ executable
        process = pb.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        parser.parse("app/src/resources/denmark-latest.osm.pbf");
        parser.filterFerryWays();

        JFrame frame = new JFrame();

        graphOfNodes = new GraphOfNodes((parser));
        graphOfNodes.addPropertyChangeListener(listener);
        frame.getContentPane().add(graphOfNodes);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300,1300);
        frame.setVisible(true);

        readerThread rd = new readerThread(reader,graphOfNodes);
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
                    System.out.println("Exitting");
                    System.exit(0);
                case "makelist":
                    //HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
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
                    //System.out.println(reader.readLine());
                    break;
                case "rundijkstra":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    lineToSend = "runDijkstra"+" " + from + " "+  to + " "+ "\n";
                    System.out.println(lineToSend);
                    writer.write(lineToSend);
                    writer.flush();

                    //graphOfNodes.setRouteToDraw(reader.readLine(), Color.red); //also draws route
                    break;
                case "runastar":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    lineToSend = "runAstar"+" " + from + " "+  to + " "+ "\n";
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(reader.readLine(), Color.green); //also draws route
                    break;
                case "runalt":
                    //System.out.println("Input from nodeId");
                    from = "3593516725";//reader1.readLine();
                    //System.out.println("Input to nodeId");
                    to = "5037683804";//reader1.readLine();
                    lineToSend = "runALT"+" " + from + " "+  to + " "+ "\n";
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
                    writer.write("runDijkstra" + "\n");
                    writer.flush();
                    writer.write(from + "\n");
                    writer.flush();
                    writer.write(to + "\n");
                    writer.flush();
                    result = reader.readLine();
                    //graphOfNodes.setRouteToDraw(result, Color.red); //also draws route
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
                    //graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
                    break;
                case "reset":
                    graphOfNodes.setImageX(0);
                    graphOfNodes.setImageY(0);
                    graphOfNodes.repaint();
                    break;
                default:
                    System.out.println("something went wrong");
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

class readerThread extends Thread {
    BufferedReader reader;
    GraphOfNodes graph;
    public readerThread(BufferedReader reader,GraphOfNodes graph){
        this.reader = reader;
        this.graph = graph;
    }
    public void run() {
        try {
        String reply;
        System.out.println("starting to read");
        while (true){
            reply = reader.readLine();
            String[] replyAsArr = reply.split(" ");
            System.out.println("got response: "+ reply);
            switch (replyAsArr[0]){
                case "Finished":
                    System.out.println("adjlist in c++ done");
                    break;
                case "path":
                    //the magical remove the first 3 the elements and cast the array to longs then to the wrapper Long and finally put it into an arrayList
                    List<Long> nodeIdLongs = Arrays.stream(Arrays.copyOfRange(replyAsArr, 3, replyAsArr.length)).mapToLong(Long::parseLong).boxed().collect(Collectors.toList());
                    switch (replyAsArr[1]){
                        case "dijkstra":
                            graph.setRouteToDraw(nodeIdLongs, Color.red);
                            break;
                        case "astar":
                            graph.setRouteToDraw(nodeIdLongs, Color.green);
                            break;
                        case "landmarks":
                            graph.setRouteToDraw(nodeIdLongs, Color.blue);
                            break;
                    }
                    break;
            }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
