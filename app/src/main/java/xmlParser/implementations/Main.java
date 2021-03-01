package xmlParser.implementations;

import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.visualization.GraphOfNodes;

import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
       XMLParserImpl parser = new XMLParserImpl();
       parser.parse("S");


       JFrame frame = new JFrame();
       frame.getContentPane().add(new GraphOfNodes(parser));

       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(1250,1200);
       frame.setVisible(true);

    }
}
