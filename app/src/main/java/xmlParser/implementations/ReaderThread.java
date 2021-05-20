package xmlParser.implementations;

import xmlParser.implementations.visualization.GraphOfNodes;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ReaderThread extends Thread {
    BufferedReader reader;
    GraphOfNodes graph;

    public ReaderThread(BufferedReader reader,GraphOfNodes graph){
        this.reader = reader;
        this.graph = graph;

    }
    public void run() {
        try {
            String reply;// = "ERROR";
            System.out.println("Reader thread ready");
            List<Long> nodesConsidered = new ArrayList<>();
            while (true){
                reply = reader.readLine();
                //if(reply.equals("null")) continue;
                //System.out.println("rply: " +reply);
                String[] replyAsArr = {"No reply"};
                if(reply!=null) {replyAsArr = reply.split(" ");}
                else System.out.println("Got reply: " + reply);
                //System.out.println("got response: "+ reply);
                switch (replyAsArr[0]){
                    case "Finished":
                        System.out.println("adjacency list loaded into c++ beep boop");
                        break;
                    case "resInFile":
                        parseResultFile("result");
                        break;
                    //DEPRECATED
                    case "path":
                        //the magical remove the first 3 the elements and cast the array to longs then to the wrapper Long and finally put it into an arrayList
                        List<Long> nodeIdLongs = Arrays.stream(Arrays.copyOfRange(replyAsArr, 3, replyAsArr.length))
                                .mapToLong(Long::parseLong)
                                .boxed()
                                .collect(Collectors.toList());
                        switch (replyAsArr[1]){
                            case "dijkstra":
                                graph.setRouteToDraw(nodeIdLongs, Color.cyan);
                                break;
                            case "astar":
                                graph.setRouteToDraw(nodeIdLongs, Color.green);
                                break;
                            case "landmarks":
                                graph.setRouteToDraw(nodeIdLongs, Color.blue);
                                break;
                        }
                        break;
                    case "info" :
                        double distance = 99999999;
                        if(!replyAsArr[1].equals("inf")) distance = Double.parseDouble(replyAsArr[1]);
                        int nodesEvaluated = Integer.parseInt(replyAsArr[2]);
                        if (replyAsArr.length ==4){
                            long chosenLandmark = Long.parseLong(replyAsArr[3]);
                            System.out.println("distance: " + distance+ " nodes evaluated: " + nodesEvaluated + " landmark "+ chosenLandmark);
                            graph.setLandmark(chosenLandmark);
                        }else{
                        System.out.println("distance: " + distance+ " nodes evaluated: " + nodesEvaluated);
                        }
                        break;
                    case "nodesConsidered" :
                        if(replyAsArr[1].equals("end")){
                            graph.setWaysToDraw(nodesConsidered, Color.red);
                            graph.repaint();
                            nodesConsidered = new ArrayList<>();
                            System.out.println("got full response");
                        }else{
                            List<Long> temporaryList = Arrays.stream(Arrays.copyOfRange(replyAsArr, 1, replyAsArr.length))
                                    .mapToLong(Long::parseLong)
                                    .boxed()
                                    .collect(Collectors.toList());
                            nodesConsidered.addAll(temporaryList);
                        }
                        break;
                    default:
                        System.out.println("Malformed input from cpp: " + reply);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseResultFile(String fileName) throws IOException {
        File resultFile = new File(fileName);
        if (resultFile.exists()){
            System.out.println("result found");
        }
        BufferedReader reader = new BufferedReader(new FileReader(resultFile));
        String line;
        String method = "";
        while ((line = reader.readLine()) != null){
            String[] resAsArr;
            resAsArr = line.split(" ");
            switch (resAsArr[0]){
                case "info":
                    method = resAsArr[1];
                    double distance = 999999999;
                    int nodesConsidered =0;
                    long landmarkChosen = -1;
                    if (!resAsArr[2].equals("inf")){
                        distance= Double.parseDouble(resAsArr[2]);
                    }
                    nodesConsidered = Integer.parseInt(resAsArr[3]);

                    String resString ="distance: " + distance+ " nodes evaluated: " + nodesConsidered;
                    if (method.equals("landmarks")){
                        landmarkChosen = Long.parseLong(resAsArr[4]);
                        resString += " landmark "+ landmarkChosen;
                        graph.setLandmark(landmarkChosen);
                    }
                    System.out.println(resString);
                    break;
                case "path":
                    List<Long> nodeIdLongs = Arrays.stream(Arrays.copyOfRange(resAsArr, 1, resAsArr.length))
                            .mapToLong(Long::parseLong)
                            .boxed()
                            .collect(Collectors.toList());
                    switch (method){
                        case "dijkstra":
                            graph.setRouteToDraw(nodeIdLongs, Color.cyan);
                            break;
                        case "astar":
                            graph.setRouteToDraw(nodeIdLongs, Color.green);
                            break;
                        case "landmarks":
                            graph.setRouteToDraw(nodeIdLongs, Color.blue);
                            break;
                        default:
                            System.out.println("Didnt understand the method used "+method + " amount of nodes " + nodeIdLongs.size());
                            break;
                    }
                    break;
                case "nodesConsid":
                    List<Long> temporaryList = Arrays.stream(Arrays.copyOfRange(resAsArr, 1, resAsArr.length))
                            .mapToLong(Long::parseLong)
                            .boxed()
                            .collect(Collectors.toList());
                    graph.setWaysToDraw(temporaryList, Color.red);
                    graph.repaint();
                    break;
            }

        }
        reader.close();
    }


}
