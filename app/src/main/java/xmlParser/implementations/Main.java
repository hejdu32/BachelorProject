package xmlParser.implementations;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.XMLParser;
import xmlParser.implementations.parsing.*;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private static List<Long> fromList = new ArrayList<>();
    private static List<Long> toList = new ArrayList<>();
    private static GraphOfNodes graphOfNodes;
    private static ProcessBuilder pb;
    private static Process process;
    private static BufferedReader reader;
    private static BufferedWriter writer;
    private static String algoOnClick = "runALT";
    private static JTextField txtFrom;
    private static JTextField txtTo;


    //DEFAULT COUNTRY TO BE PARSED
    private static String country = "denmark";

    public static void main(String[] args) throws IOException, FactoryException {//TransformException
        country = args[0];
        String pathToExe = args[1];
        Main listener = new Main();
        XMLParserImpl parser = new XMLParserImpl();
        pb = new ProcessBuilder();
        pb.command(pathToExe);  // C++ executable
        process = pb.start();
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        parser.parse("app/src/resources/"+country+"-latest.osm.pbf");

        parser.filterFerryWays();
        GraphBuilder graphBuilder = new GraphBuilder(parser);

        //HashMap<Long, List<Edge>> reducedAdjlist = null;
        //try {
        //    reducedAdjlist = graphBuilder.simpleReduceAdjacencyList(parser.getNodes(),new ArrayList<>(parser.getWays().values()));
        //} catch (TransformException e) {
        //    e.printStackTrace();
        //}
//
        //System.out.println("adjList size: " + reducedAdjlist.keySet().size());
        //graphOfNodes.setAdjacencyList(adjList);


        File possibleCountryFile = new File(country);
        String pathToCppFile;
        if (!possibleCountryFile.exists()){
            graphBuilder.writeAllWays(country,parser.getNodes(),new ArrayList<>(parser.getWays().values()));
            //graphBuilder.writeReducedList(country,parser.getNodes(),reducedAdjlist);
        }
        pathToCppFile= possibleCountryFile.getAbsolutePath().replaceAll("\\\\","/");

        //6System.out.println("makeAdjacencyList " + pathToCppFile + "\n");
        writer.write("makeAdjacencyList " + pathToCppFile + "\n");
        writer.flush();

        JFrame frame = new JFrame();

        graphOfNodes = new GraphOfNodes((parser));
        graphOfNodes.addPropertyChangeListener(listener);

        System.out.println("Making ");
        addUIComponents();

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
                case "algo":
                    System.out.println("type algo (runDijkstra, runAstar, runALT): ");
                    String inputAlgo = inputReader.readLine().toLowerCase();
                    String inputAlgoLower = inputAlgo.toLowerCase();
                    switch (inputAlgoLower) {
                        case "rundijkstra":
                            algoOnClick = "runDijkstra";
                            System.out.println("Algorithm on click is now: " + "runDijkstra");
                            break;
                        case "runastar":
                            algoOnClick = "runAstar";
                            System.out.println("Algorithm on click is now: " + "runAstar");
                            break;
                        case "runalt":
                            algoOnClick = "runALT";
                            System.out.println("Algorithm on click is now: " + "runALT");
                            break;
                        default:
                            System.out.println("Has to be either runDijksta, runAstar, runALT. Got: " + inputAlgo);
                            break;
                    }
                    break;
                case "reverse":
                    System.out.println("Reversing " + algoOnClick + " " + to + " "+  from);
                    String from = Main.from;
                    String to = Main.to;
                    lineToSend = algoOnClick+" " + to + " "+  from + "\n";
                    Main.from = to;
                    Main.to = from;
                    System.out.println(lineToSend);
                    writer.write(lineToSend);
                    writer.flush();
                    //graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
                    break;
                case "run":
                    System.out.println("type algo (runDijkstra, runAstar, runALT) From node: To node: ");
                    String all = inputReader.readLine();
                    String[] splitInput = all.split("\\s+");
                    String algo = splitInput[0].toLowerCase();
                    boolean correctSize = splitInput.length==3;
                    boolean correctAlgo = algo.equals("rundijkstra") || algo.equals("runastar") || algo.equals("runalt");
                    if(correctSize && correctAlgo) {
                        lineToSend = splitInput[0] + " " + splitInput[1] + " " + splitInput[2] + "\n";
                        System.out.println(lineToSend);
                        writer.write(lineToSend);
                        writer.flush();
                    } else System.out.println("Did not understand input: " + all);
                    //graphOfNodes.setRouteToDraw(result, Color.green); //also draws route
                    break;
                case "reset":
                    graphOfNodes.setImageX(0);
                    graphOfNodes.setImageY(0);
                    //try {
                    //HashMap<Long, List<Edge>> adjList = graphBuilder.simpleReduceAdjacencyList(parser.getNodes(),new ArrayList<>(parser.getWays().values()));
                    //System.out.println("nodesToSearch size: " + parser.getNodesToSearchFor().keySet().size());
                    //System.out.println("adjList size: " + adjList.keySet().size());
                    //graphOfNodes.setAdjacencyList(adjList);
                    //} catch (TransformException e) {
                    //    e.printStackTrace();
                    //}
                    graphOfNodes.repaint();
                    break;
                default:
                    System.out.println("malformed input: " +input);
                    break;
            }
        }
    }

    private static void addUIComponents() {
        txtFrom = new JTextField();
        txtTo = new JTextField();
        txtFrom.setPreferredSize(new Dimension(300, 30));
        txtTo.setPreferredSize(new Dimension(300, 30));
        graphOfNodes.add(txtFrom);
        graphOfNodes.add(txtTo);

        JButton buttonDijkstra = new JButton("Dijkstra");
        JButton buttonAStar = new JButton("A*");
        JButton buttonALT = new JButton("ALT");
        graphOfNodes.add(buttonDijkstra);
        graphOfNodes.add(buttonAStar);
        graphOfNodes.add(buttonALT);
        buttonDijkstra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean onlyNumbers = !txtFrom.getText().matches("[a-zA-Z_]+") && !txtTo.getText().matches("[a-zA-Z_]+");
                if (onlyNumbers) {
                    runAlgo(txtFrom, txtTo, "runDijkstra");
                } else System.out.println("Did not understand nodes");
            }
        });
        buttonAStar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean onlyNumbers = !txtFrom.getText().matches("[a-zA-Z_]+") && !txtTo.getText().matches("[a-zA-Z_]+");
                if (onlyNumbers) {
                    runAlgo(txtFrom, txtTo, "runAstar");
                } else System.out.println("Did not understand nodes");
            }
        });
        buttonALT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean onlyNumbers = !txtFrom.getText().matches("[a-zA-Z_]+") && !txtTo.getText().matches("[a-zA-Z_]+");
                if (onlyNumbers) {
                    runAlgo(txtFrom, txtTo, "runALT");
                } else System.out.println("Did not understand nodes");
            }
        });
    }

    private static void runAlgo(JTextField txtFrom, JTextField txtTo, String runAstar) {
        from = txtFrom.getText();
        to = txtTo.getText();
        String lineToSend = runAstar + " " + from + " " + to + "\n";
        try {
            writer.write(lineToSend);
            writer.flush();
            System.out.println(lineToSend);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("red")){
            txtFrom.setText(graphOfNodes.getFrom());
        }
        if(evt.getPropertyName().equals("blue")){
            txtTo.setText(graphOfNodes.getTo());
        }

        if(evt.getPropertyName().equals("SecoundClick")) {
            XMLParser parser = new XMLParserImpl();
            try {
                parser.parse("app/src/resources/denmark-latest.osm.pbf");
                GraphBuilder graphBuilder = null;
                graphBuilder = new GraphBuilder(parser);
                HashMap<Long, List<Edge>> adjList = graphBuilder.createAdjacencyList();
                graphOfNodes.setReducedAdjList(adjList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Running with " + algoOnClick);
            fromList = graphOfNodes.getFromNodes();
            int fromSize = fromList.size();
            toList = graphOfNodes.getToNodes();
            int toSize = toList.size();
            from = "";
            for (Long id :  fromList){
                from = from + " " + id;
            }
            System.out.println(from);
            to = "";
            for (Long id :  toList){
                to = to + " " + id;
            }
            System.out.println(to);
            try {
                String lineToSend = algoOnClick+" "  + fromList.get(0) + " "  +  toList.get(0) + "\n";
                System.out.println(lineToSend);
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

