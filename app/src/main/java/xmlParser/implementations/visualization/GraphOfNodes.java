package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Iterator;


public class GraphOfNodes extends JPanel{

    private final XMLParserImpl parser;
    private Graphics2D graph2d;
    private BufferedImage bufferedImage;
    private int imageX = 0;
    private int imageY = 0;
    private boolean isGraphDrawn = false;
    private int pressedX;
    private int pressedY;


    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        this.bufferedImage = new BufferedImage(1200,950,BufferedImage.TYPE_INT_RGB);
        this.graph2d = bufferedImage.createGraphics();
        addMouseListener(new MouseAdapter() {


            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressedX = e.getX();
                pressedY = e.getY();
            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                imageX += e.getX() - pressedX;
                imageY += e.getY() - pressedY;
                pressedX = e.getX();
                pressedY = e.getY();
                repaint();
            }
        });


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!isGraphDrawn) {
            drawBackground();
            drawGraph();
            flipYCoordinate();
            isGraphDrawn = true;
        }

        g.drawImage(bufferedImage,imageX,imageY,this);
    }

    private void flipYCoordinate() {
        AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
    }

    private void drawBackground() {
        graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
    }

    private void drawGraph() {
        graph2d.setColor(Color.BLACK);
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

                    int scaleFactor = 380;
                    int yOffset = 6049800;
                    int xOffset = 441800;
                    Shape l = new Line2D.Double((prevX- xOffset)/ scaleFactor, (prevY- yOffset)/ scaleFactor, (currX- xOffset)/ scaleFactor, (currY- yOffset)/ scaleFactor);
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }
        }

    public int getImageX() {
        return imageX;
    }

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    public int getImageY() {
        return imageY;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }


}
































