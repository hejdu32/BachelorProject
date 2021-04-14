package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.util.DrawingUtil;
import xmlParser.implementations.util.NodeFinderImpl;
import xmlParser.implementations.util.ViewLimiterImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class GraphOfNodes extends JPanel{


    private final XMLParserImpl parser;
    private final ViewLimiterImpl viewLimiter;
    private final NodeFinderImpl nodeFinder;
    private BufferedImage bufferedImage;
    private boolean isGraphDrawn = false;
    private int pressedX;
    private int pressedY;
    private double zoomFactor = 1.0;
    private boolean zoomable = true;
    private int fullResolutionFactor = 8;
    private int fullResolution = 1300;
    private int fullResolutionX = 1300 * fullResolutionFactor;
    private int fullResolutionY = 1300 * fullResolutionFactor;
    private int imageX = 0;
    private int imageY = 0;
    private double oldzoom = 0;
    private int yOffset;
    private int xOffset;
    private double windowScale;
    private List<CustomWay> ways;
    private boolean firstClick = true;
    private int drawX;
    private int drawY;
    private boolean clicked = false;
    private int redDrawX;
    private int redDrawY;
    private int blueDrawX;
    private int blueDrawY;
    private List<Long> redPart = new ArrayList<>();

    /*
    * Contructor
    *
    * */
    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        ways = new ArrayList<>(parser.getWays().values());
        this.bufferedImage = new BufferedImage(fullResolutionX,fullResolutionY,BufferedImage.TYPE_INT_ARGB);
        viewLimiter = new ViewLimiterImpl(ways, parser.getNodes());
        nodeFinder = new NodeFinderImpl();

        viewLimiter.setMargin(); //finds lowestXY and highest XY
        yOffset = (int) viewLimiter.getLowestY();
        xOffset = (int) viewLimiter.getLowestX();
        windowScale = viewLimiter.calculateScale(fullResolution);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                zoomable = false;
                pressedX = e.getX();
                pressedY = e.getY();
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON2){
                    if (firstClick) {
                        clicked = true;
                        drawX = e.getX()*fullResolutionFactor;
                        drawY = e.getY()*fullResolutionFactor;
                        firstClick = false;
                        isGraphDrawn = false;
                        repaint();
                    } else {
                        clicked = true;
                        drawX = e.getX()*fullResolutionFactor;
                        drawY = e.getY()*fullResolutionFactor;
                        firstClick = true;
                        isGraphDrawn = false;
                        repaint();
                    }
                }
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON3){
                    //####TEST OF VIEWLIMITER#####
                    System.out.println("################testing viewLimiter################");
                    System.out.println("Before: " + ways.size());
                    viewLimiter.setMargin(0,0);
                    ways = viewLimiter.limitToRelevantWays(scaleValueX(redDrawX), scaleValueY(redDrawY), scaleValueX(blueDrawX), scaleValueY(blueDrawY), xOffset, yOffset, windowScale / fullResolutionFactor);
                    System.out.println("After: " + ways.size());
                    isGraphDrawn = false;
                    clicked = true;
                    repaint();
                }
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

    public void setRedPart(String redPart) {
        System.out.println("beep"+ redPart);
        List<String> nodeIds = Arrays.asList(redPart.split("\\s+"));
        List<String> nodeIdsNOSPACE = nodeIds.subList(1, nodeIds.size()); //remove head of list as it is an identifier
        List<Long> nodeIdLongs = new ArrayList<>();

        for(String id : nodeIdsNOSPACE) {
            nodeIdLongs.add(Long.parseLong(id));
        }

        //for (CustomNode node :                parser.getNodes().values()) {
        //    nodeIdLongs.add(node.getId());
        //}

        paintRedWay(nodeIdLongs);

        repaint();
    }

    private double scaleValueX(double x){
        return ((x)+imageX*fullResolutionFactor)/zoomFactor;
    }

    private double scaleValueY(double y){
        return ((y )+imageY*fullResolutionFactor)/zoomFactor;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!isGraphDrawn) {
            //setScopeOfImage();
            drawBackground();
            paintRedWay(redPart);
            drawGraph();
            bufferedImage = DrawingUtil.flipYCoordinate(bufferedImage);
        }
        if(!isGraphDrawn|| clicked) {
            drawRedAndBlue();
            clicked = false;
        }

        if (zoomable) {
            zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
        }

        System.out.println("ImageX and Y: " + imageX + "," + imageY);
        g.drawImage(bufferedImage, 0, 0, 1300, 1300,
                (int) ((imageX*fullResolutionFactor ) / zoomFactor),
                (int) ((imageY*fullResolutionFactor ) / zoomFactor),
                (int) (((imageX*fullResolutionFactor + 1300*fullResolutionFactor) )/ zoomFactor ),
                (int) (((imageY*fullResolutionFactor + 1300*fullResolutionFactor) )/ zoomFactor ),
                this);

    }

    private void drawRedAndBlue() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setColor(Color.RED);
        if (!firstClick) {
            redDrawX = drawX; redDrawY = drawY;
            System.out.println("Red: " + redDrawX + "," + (redDrawY));
        }
        Shape red = new Ellipse2D.Double(scaleValueX(redDrawX-25), scaleValueY(redDrawY-25), 50, 50);
        graph2d.fill(red);
        graph2d.draw(red);

        graph2d.setColor(Color.BLUE);
        if (firstClick) {
            blueDrawX = drawX; blueDrawY = drawY;
            System.out.println("Blue: " + (blueDrawX) + "," + (blueDrawY));
        }
        Shape blue = new Ellipse2D.Double(scaleValueX(blueDrawX-25), scaleValueY(blueDrawY-25), 50, 50);
        graph2d.fill(blue);
        graph2d.draw(blue);

        graph2d.dispose();
    }

    private void zoomCalculation(double zoomFactor) {
        int x1 =        (int) ((imageX));
        int y1 =        (int) ((imageY));
        int x2 =        (int) (((imageX + 130)));
        int y2 =        (int) (((imageY + 130)));
        int deltaX =    (int) ((x2 - x1));
        int deltaY =    (int) ((y2 - y1));
        double zoomDelta = oldzoom-zoomFactor;
        if (zoomDelta < 0){
            imageX = (int) ((x1 + 0.40 * deltaX));
            imageY = (int) ((y1 + 0.40 * deltaY));
            oldzoom = zoomFactor;
        }
        else if (zoomDelta > 0){
            imageX = (int) ((x1 - 0.40 * deltaX));
            imageY = (int) ((y1 - 0.40 * deltaY));
            oldzoom = zoomFactor;
        }
    }



    private void drawBackground() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
        graph2d.dispose();
    }

    private void paintRedWay(List<Long> route) {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(15));
        int alpha = 127; // 50% transparent
        Color myColour = new Color(255, 0, 0, alpha);
        graph2d.setColor(myColour);
        if(route.size()>0) {
            long previousId = 0L;
            Iterator iterator = route.iterator();
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

                    double scaleFactor =  (windowScale / fullResolutionFactor);
                    int yOffset = this.yOffset;
                    int xOffset = this.xOffset;
                    Shape l = new Line2D.Double(((prevX-xOffset)/ scaleFactor),
                            Math.abs(((prevY-yOffset)/ scaleFactor)-fullResolutionY),
                            ((currX-xOffset)/ scaleFactor),
                            Math.abs(((currY-yOffset)/ scaleFactor)-fullResolutionY) );
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }
        graph2d.dispose();
    }

    private void drawGraph() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(2));
        graph2d.setColor(Color.BLACK);
        for (CustomWay way: ways) {
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

                    double scaleFactor =  (windowScale / fullResolutionFactor);

                    int yOffset = (int) viewLimiter.getLowestY();
                    int xOffset = (int) viewLimiter.getLowestX();

                    Point prevPoint = nodeFinder.convertCoordsXYToImageXY(prevX, prevY, xOffset, yOffset, scaleFactor);
                    Point currPoint = nodeFinder.convertCoordsXYToImageXY(currX, currY, xOffset, yOffset, scaleFactor);

                    Shape l = new Line2D.Double(prevPoint.x,
                                                prevPoint.y,
                                                currPoint.x,
                                                currPoint.y);
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }


        graph2d.dispose();

        isGraphDrawn = true;
    }


    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }


    public String getFrom() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint((redDrawX), (redDrawY), parser.getNodes(), xOffset, yOffset, windowScale /fullResolutionFactor));
    }
    public String getTo() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint((blueDrawX), (blueDrawY), parser.getNodes(), xOffset, yOffset, windowScale /fullResolutionFactor));
    }
}
































