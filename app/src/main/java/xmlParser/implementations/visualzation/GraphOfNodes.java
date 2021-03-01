package xmlParser.implementations.visualzation;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;


public class GraphOfNodes extends JPanel{

    private XMLParserImpl parser;
    private Graphics2D graph2d;

    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image img = drawBackground();

        drawGraph(graph2d,parser);
        g.drawImage(img,10,10,this);
        System.out.println("image has been dawn");
    }

    private Image drawBackground() {
        BufferedImage bufferedImage = new BufferedImage(1200,1200,BufferedImage.TYPE_INT_RGB);
        graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());

        return bufferedImage;
    }

    private void drawGraph(Graphics2D g, XMLParserImpl parser) {
        this.parser = parser;
        g.setColor(Color.BLACK);
        System.out.println("drawGraph has been called");
        for (CustomWay way:parser.getWays()) {
            long previousId = 0L;
            Iterator iterator = way.getNodeIdList().iterator();
            do {
                long currId = (long) iterator.next();
                if(previousId == 0L) {
                    previousId = currId;
                }
                else {
                    CustomNode previousNode = parser.getNodes().get(previousId);
                    double prevX = previousNode.getLatitudeAsXCoord();
                    double prevY = previousNode.getLongtitudeAsYCoord();

                    CustomNode currNode = parser.getNodes().get(currId);
                    double currX = currNode.getLatitudeAsXCoord();
                    double currY = currNode.getLongtitudeAsYCoord();
                    //System.out.println(prevX/1000);
                    //System.out.println(prevY/10000);
                    //draw line
                    Shape l = new Line2D.Double((prevX-441800)/380, (prevY-6049800)/380, (currX-441800)/380, (currY-6049800)/380);
                    //System.out.println("shape added");
                    g.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }
        }
}
































