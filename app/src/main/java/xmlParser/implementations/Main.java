package xmlParser.implementations;

import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.testImplementation.XMLVisualizationStump;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {
    private double scalingFactor = 1.0;

    public double getScalingFactor(){
        return scalingFactor;
    }

    public static void main(String[] args) throws FileNotFoundException {
       XMLParserImpl parser = new XMLVisualizationStump();
       parser.parse("S");


       JFrame frame = new JFrame();
       GraphOfNodes graphOfNodes = new GraphOfNodes((parser));
       frame.getContentPane().add(graphOfNodes);

       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(1250,1200);
       frame.setVisible(true);

    }
}
