package xmlParser.implementations;

import xmlParser.implementations.visualization.GraphOfNodes;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
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
                        //System.out.println("replyAsArr: " + replyAsArr);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
