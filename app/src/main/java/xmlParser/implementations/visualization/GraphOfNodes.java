package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;


public class GraphOfNodes extends JPanel{


    private final XMLParserImpl parser;
    //private Graphics2D graph2d;
    private BufferedImage bufferedImage;
    private boolean isGraphDrawn = false;
    private int pressedX;
    private int pressedY;
    private double zoomFactor = 1.0;
    private int zoomOffSetX = 0;
    private int zoomOffSetY = 0;
    private boolean zoomable = true;
    private BufferedImage scaledImage;
    private static int fullResolutionFactor = 8;
    private static int fullResolutionX = 1300 * fullResolutionFactor;
    private static int fullResolutionY = 1000 * fullResolutionFactor;
    private static int currentResolutionX = fullResolutionX;
    private static int currentResolutionY = fullResolutionY;
    private int imageX = 1300/2;
    private int imageY = 1000/2;
    private boolean isDrawnAgain = false;
    private boolean isDrawnAgain2 = false;
    private double oldzoom = 0;

    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        this.bufferedImage = new BufferedImage(fullResolutionX,fullResolutionY,BufferedImage.TYPE_INT_ARGB);

        //graph2d = bufferedImage.createGraphics();
        addMouseListener(new MouseAdapter() {


            @Override
            public void mousePressed(MouseEvent e) {
                pressedX = e.getX();
                pressedY = e.getY();
                zoomable = false;
            }
            @Override
            public void mouseReleased(MouseEvent e){
                zoomable = true;
            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                //boolean notOutsideX = imageX + e.getX() - pressedX + 1300 <= currentResolutionX;// & imageX + e.getX() - pressedX >= 0;
                //boolean notOutsideY = imageY + e.getY() - pressedY + 1000 <= currentResolutionY;// & imageY + e.getY() - pressedY >= 0;
                //if (notOutsideX)
                //if (notOutsideY)
                imageX += pressedX - e.getX();
                imageY += pressedY - e.getY();
                pressedX = e.getX();
                pressedY = e.getY();
                repaint();
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            //Broken zoom stuff
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() > 0) {
                    zoomFactor = 0.8 * zoomFactor;
                    zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
                }
                else if (e.getWheelRotation() < 0) {
                    zoomFactor = 1.2 * zoomFactor +0.01;
                    zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
                }
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
        }
        if (zoomable) {
            zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
            System.out.println("Scale : " + zoomFactor);
            zoomCalculation(zoomFactor);
            //System.out.println("imageX: " + imageX + " imageY: " + imageY);
            //currentResolutionX = (int) (fullResolutionX*zoomFactor)/fullResolutionFactor;
            //currentResolutionY = (int) (fullResolutionY*zoomFactor)/fullResolutionFactor;
        }
        System.out.println("imageX: " + imageX + " imageY: " + imageY);
        //g.drawImage(subImage,0,0,this);
        g.drawImage(bufferedImage, 0, 0, 1300, 1000,
                (int) ((imageX ) / zoomFactor),
                (int) ((imageY ) / zoomFactor),
                (int) (((imageX + 1300) ) / zoomFactor),
                (int) (((imageY + 1000) ) / zoomFactor),
                this);

    }

    private void zoomCalculation(double zoomFactor) {
        int x1 =        (int) ((imageX));
        int y1 =        (int) ((imageY));
        int x2 =        (int) (((imageX + 130)));
        int y2 =        (int) (((imageY + 100)));
        int deltaX =    (int) ((x2 - x1));
        int deltaY =    (int) ((y2 - y1));
        double zoomDelta = oldzoom-zoomFactor;
        System.out.println("Zoom delta: "+ (zoomDelta));
        if (zoomDelta < 0){
            System.out.println("ind");
                    imageX = (int) ((x1 + 0.40 * deltaX));
                    imageY = (int) ((y1 + 0.40 * deltaY));
            oldzoom = zoomFactor;
        }
        else if (zoomDelta > 0){
            System.out.println("out");
                    imageX = (int) ((x1 - 0.40 * deltaX));
                    imageY = (int) ((y1 - 0.40 * deltaY));
            oldzoom = zoomFactor;
        }
    }

    private void flipYCoordinate() {
        AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
    }


    private void drawBackground() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
        graph2d.dispose();
    }


    private void drawGraph() {
        Graphics2D graph2d = bufferedImage.createGraphics();;
        graph2d.setColor(Color.BLACK);
        //for (CustomWay way:parser.getWays()) {
        //    long previousId = 0L;
        //    Iterator iterator = way.getNodeIdList().iterator();
        //    do {
        //        long currId = (long) iterator.next();
        //        if(previousId == 0L) {
        //            previousId = currId;
        //        }
        //        else {
        //            CustomNode previousNode = parser.getNodes().get(previousId);
        //            double prevX = previousNode.getLatitudeAsXCoord();
        //            double prevY = previousNode.getLongtitudeAsYCoord();
//
        //            CustomNode currNode = parser.getNodes().get(currId);
        //            double currX = currNode.getLatitudeAsXCoord();
        //            double currY = currNode.getLongtitudeAsYCoord();
//
        //            int scaleFactor = (int) (380/ fullResolutionFactor);
        //            int yOffset = 6049800;
        //            int xOffset = 441800;
        //            Shape l = new Line2D.Double(((prevX-xOffset)/ scaleFactor) , ((prevY-yOffset)/ scaleFactor) , ((currX-xOffset)/ scaleFactor) , ((currY-yOffset)/ scaleFactor) );
        //            graph2d.draw(l);
        //            previousId = currId;
        //        }
        //    } while(iterator.hasNext()); }

        File img = new File("C:/Users/svend/OneDrive/Uni/6. semester/Bachelor Project/999633.jpg");
        try {
            bufferedImage = ImageIO.read(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isGraphDrawn = true;

        graph2d.dispose();
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
































