package xmlParser.implementations;

import org.hsqldb.persist.Log;
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
            String reply;
            System.out.println("starting to read");
            List<Long> nodesConsidered = new ArrayList<>();
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
                        List<Long> nodeIdLongs = Arrays.stream(Arrays.copyOfRange(replyAsArr, 3, replyAsArr.length))
                                .mapToLong(Long::parseLong)
                                .boxed()
                                .collect(Collectors.toList());
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
                    case "nodesConsidered" :
                        if(replyAsArr[2] == "end"){
                            //graph.drawMeLikeOneOfYourFrenchGirls(listOfNodes);
                            nodesConsidered = new ArrayList<>();
                            break;
                        }else{
                            List<Long> templst = Arrays.stream(Arrays.copyOfRange(replyAsArr, 1, replyAsArr.length))
                                    .mapToLong(Long::parseLong)
                                    .boxed()
                                    .collect(Collectors.toList());
                            nodesConsidered.addAll(templst);
                            break;
                        }
                    default:
                        System.out.println("malformed input from cpp: " + reply);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
