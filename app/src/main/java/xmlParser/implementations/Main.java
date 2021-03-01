package xmlParser.implementations;

import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.testImplementation.XMLParserStump;
import xmlParser.implementations.testImplementation.XMLstumpRealData;
import xmlParser.implementations.visualzation.GraphOfNodes;

import javax.swing.*;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
       XMLParserImpl parser = new XMLstumpRealData();
       parser.parse("S");


       JFrame frame = new JFrame();
       frame.getContentPane().add(new GraphOfNodes(parser));

       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(1200,1200);
       frame.setVisible(true);

        System.out.println("main done image should be completely done");
    }
}
