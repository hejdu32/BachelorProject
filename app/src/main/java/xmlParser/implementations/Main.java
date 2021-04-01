package xmlParser.implementations;

import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import xmlParser.framework.CoordinateCodes;
import xmlParser.framework.DistanceCalculator;
import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.Edge;
import xmlParser.implementations.parsing.XMLParserImpl;
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

    public static void main(String[] args) throws IOException, TransformException {
        XMLParserImpl parser = new XMLParserStump();
        //XMLParserImpl parser = new XMLParserImpl();
       //parser.parse("S");

        var pb = new ProcessBuilder();
        pb.command("C:/proj/BachelorCpp/app/build/exe/test/appTest.exe");  // C++ executable
        var process = pb.start();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader reader1 = new BufferedReader(new InputStreamReader(System.in));
        boolean reading = true;
        System.out.println("###########################################################################\n");
        while(reading){
            String input = reader1.readLine();
            if(input.equalsIgnoreCase("Exit")){
                process.destroy();
                reading = false;
            }
            else {
                writer.write(input + "\n");
                writer.flush();
                System.out.println(reader.readLine());

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

