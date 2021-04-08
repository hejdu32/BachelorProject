package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;
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
    private int zoomOffSetX = 0;
    private int zoomOffSetY = 0;
    private boolean zoomable = true;
    private BufferedImage scaledImage;
    private static int fullResolutionFactor = 1;
    private static int fullResolutionX = 1300 * fullResolutionFactor;
    private static int fullResolutionY = 1300 * fullResolutionFactor;
    private static int currentResolutionX = fullResolutionX;
    private static int currentResolutionY = fullResolutionY;
    private int imageX = 0;
    private int imageY = 0;
    private boolean isDrawnAgain = false;
    private boolean isDrawnAgain2 = false;
    private double oldzoom = 0;
    private int yOffset = 6049800;
    private int xOffset = 441800;
    private double scaleTo1300 = 380;
    private List<CustomWay> ways;
    private boolean firstClick = true;
    private int drawX;
    private int drawY;
    private boolean secoundClick = false;
    private boolean clicked = false;
    private int redDrawX;
    private int redDrawY;
    private int blueDrawX;
    private int blueDrawY;

    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        ways = parser.getWays();
        System.out.println("Before: " + ways.size());
        this.bufferedImage = new BufferedImage(fullResolutionX,fullResolutionY,BufferedImage.TYPE_INT_ARGB);
        viewLimiter = new ViewLimiterImpl(parser.getWays(), parser.getNodes());
        nodeFinder = new NodeFinderImpl();
        setScopeOfImage();

        addMouseListener(new MouseAdapter() {


            @Override
            public void mousePressed(MouseEvent e) {
                zoomable = false;
                pressedX = e.getX();
                pressedY = e.getY();
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON2){
                    System.out.println("click");
                    if (firstClick) {
                        clicked = true;
                        System.out.println("firstclick");
                        drawX = e.getX()*fullResolutionFactor;
                        drawY = e.getY()*fullResolutionFactor;
                        firstClick = false;
                        isGraphDrawn = false;
                        repaint();
                    } else {
                        clicked = true;
                        System.out.println("secoundclick");
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
                    ways = viewLimiter.limitToRelevantWays(scaleValueX(redDrawX), scaleValueY(redDrawY), scaleValueX(blueDrawX), scaleValueY(blueDrawY), xOffset, yOffset, scaleTo1300/fullResolutionFactor);
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

    private double scaleValueX(double x){
        return ((x / fullResolutionFactor)+imageX)/zoomFactor;
    }

    private double scaleValueY(double y){
        return ((y / fullResolutionFactor)+imageY)/zoomFactor;
    }

    private void setScopeOfImage() {
        viewLimiter.setMargin();
        yOffset = (int) viewLimiter.getLowestY();
        xOffset = (int) viewLimiter.getLowestX();

        double xOffsetDiff = viewLimiter.getHighestX() - viewLimiter.getLowestX();
        double yOffsetDiff = viewLimiter.getHighestY() - viewLimiter.getLowestY();
        double diffToUse = 0.0;
        diffToUse = Math.max((xOffsetDiff), (yOffsetDiff));
        scaleTo1300 = (diffToUse/1300);
        System.out.println(scaleTo1300 + " THIS IS THE SCALE");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!isGraphDrawn) {
            drawBackground();
            drawGraph();
            flipYCoordinate();
            setScopeOfImage();
        }
        System.out.println(clicked);
        if(!isGraphDrawn|| clicked) {
            drawRedAndBlue();
            clicked = false;
        }

        if (zoomable) {
            zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
            System.out.println("Scale : " + zoomFactor);
            zoomCalculation(zoomFactor);
            System.out.println("imageX: " + imageX + " imageY: " + imageY);
            //if (zoomFactor >= 0.8) {
            //    isGraphDrawn = false;
            //}
            //currentResolutionX = (int) (fullResolutionX*zoomFactor)/fullResolutionFactor;
            //currentResolutionY = (int) (fullResolutionY*zoomFactor)/fullResolutionFactor;
        }
        System.out.println("imageX: " + imageX + " imageY: " + imageY);

        g.drawImage(bufferedImage, 0, 0, 1300, 1300,
                (int) ((imageX ) / zoomFactor),
                (int) ((imageY ) / zoomFactor),
                (int) (((imageX + 1300) ) / zoomFactor),
                (int) (((imageY + 1300) ) / zoomFactor),
                this);

    }

    private void drawRedAndBlue() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setColor(Color.RED);
        if (!firstClick) {
            redDrawX = drawX; redDrawY = drawY;
        }
        Shape red = new Ellipse2D.Double(scaleValueX(redDrawX), scaleValueY(redDrawY), 50, 50);
        graph2d.fill(red);
        graph2d.draw(red);

        graph2d.setColor(Color.BLUE);
        if (firstClick) {
            blueDrawX = drawX; blueDrawY = drawY;
        }
        Shape blue = new Ellipse2D.Double(scaleValueX(blueDrawX), scaleValueY(blueDrawY), 50, 50);
        graph2d.fill(blue);
        graph2d.draw(blue);

        graph2d.dispose();
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
        Graphics2D graph2d = bufferedImage.createGraphics();
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

                    double scaleFactor =  (scaleTo1300 / fullResolutionFactor);
                    int yOffset = this.yOffset;
                    int xOffset = this.xOffset;
                    Shape l = new Line2D.Double(((prevX-xOffset)/ scaleFactor) , ((prevY-yOffset)/ scaleFactor) , ((currX-xOffset)/ scaleFactor) , ((currY-yOffset)/ scaleFactor) );
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }

        //File img = new File("C:/Users/svend/OneDrive/Uni/6. semester/Bachelor Project/999633.jpg");
        //try {
        //    bufferedImage = ImageIO.read(img);
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //graph2d.setColor(Color.GREEN);
        //long greennodeid = nodeFinder.findClosestNodeToPoint(5000,5000,parser.getNodes(),xOffset,yOffset,scaleTo1300/fullResolutionFactor);
        //CustomNode greennode = parser.getNodes().get(greennodeid);
        //Point greenpoint = nodeFinder.findClosestPointToNode((int) greennode.getLatitudeAsXCoord(), (int) greennode.getLongtitudeAsYCoord(),xOffset,yOffset,scaleFactor);
        //Shape green = new Ellipse2D.Double(greenpoint.x, greenpoint.y, 50, 50);
//
        //graph2d.fill(green);
        //graph2d.draw(green);


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
































