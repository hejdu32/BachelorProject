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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;


public class GraphOfNodes extends JPanel{


    private final XMLParserImpl parser;
    private final ViewLimiterImpl viewLimiter;
    private final NodeFinderImpl nodeFinder;
    private BufferedImage prerenderedImage;
    private BufferedImage bufferedImage;
    private boolean isGraphDrawn = false;
    private int pressedX;
    private int pressedY;
    private double zoomFactor = 1.0;
    private boolean zoomable = true;
    private int fullResolutionFactor = 1;//cant be change for some reason
    private int viewResolution = 1300; //cant be change for some reason
    private int fullResolutionX = viewResolution * fullResolutionFactor;
    private int fullResolutionY = viewResolution * fullResolutionFactor;
    private double imageX = 0;
    private double imageY = 0;
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
    private Color myColor = Color.red;
    private List<Point> tilePoints = new ArrayList<>();
    private Map<Point, BufferedImage> tiles = new HashMap<>();
    private Map<Point, Double> tileZoom = new HashMap<>();
    private int zoomLevel = 1;
    private boolean mouseReleased = false;
    private int tileRes = 100;
    private boolean inited = false;
    private Map<Point, Boolean> drawTiles = new HashMap<>();
    private Map<Point, List<CustomWay>> tileWays = new HashMap<>();
    private Map<Point, List<Shape>> tileShapes = new HashMap<>();


    /*
    * Contructor
    *
    * */
    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        ways = new ArrayList<>(parser.getWays().values());
        this.bufferedImage = new BufferedImage(fullResolutionX,fullResolutionY,BufferedImage.TYPE_INT_ARGB);
        this.prerenderedImage = new BufferedImage(fullResolutionX*8,fullResolutionY*8,BufferedImage.TYPE_INT_ARGB);
        viewLimiter = new ViewLimiterImpl(ways, parser.getNodes());
        nodeFinder = new NodeFinderImpl();
        setBackground(Color.WHITE);
        for (int i = 0; i<viewResolution; i=i+100){
            for (int j = 0; j<viewResolution; j=j+100){
                Point point = new Point(i,j);
                tilePoints.add(point);
                if(i/tileRes %2 == 0 & j/tileRes %2 == 0 | i/tileRes %2 == 1 & j/tileRes %2 == 1) {
                    BufferedImage biggerImage = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
                    tiles.put(point, biggerImage);
                    tileZoom.put(point,1.0);
                }
                else tiles.put(point, new BufferedImage(tileRes,tileRes, BufferedImage.TYPE_INT_ARGB));
                tileZoom.put(point,1.0);

            }
        }
        viewLimiter.setMargin(); //finds lowestXY and highest XY
        yOffset = (int) viewLimiter.getLowestY();
        xOffset = (int) viewLimiter.getLowestX();
        windowScale = viewLimiter.calculateScale(viewResolution);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                zoomable = false;
                pressedX = e.getX();
                pressedY = e.getY();
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON2){
                    //####TEST OF VIEWLIMITER#####
                    System.out.println("################testing viewLimiter################");
                    System.out.println("Before: " + ways.size());
                    viewLimiter.setMargin(0,0);
                    ways = viewLimiter.limitToRelevantWays(scaleValueXNoZoom(redDrawX), scaleValueYNoZoom(redDrawY), scaleValueXNoZoom(blueDrawX), scaleValueYNoZoom(blueDrawY), xOffset, yOffset, windowScale / fullResolutionFactor);
                    System.out.println("After: " + ways.size());
                    isGraphDrawn = false;
                    clicked = true;
                    repaint();
                }
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON3){
                    if (firstClick) {
                        clicked = true;
                        drawX = (int) (e.getX()*fullResolutionFactor);
                        drawY = (int) (e.getY()*fullResolutionFactor);
                        firstClick = false;
                        System.out.println("redrawing");
                        //isGraphDrawn = false;
                        repaint();
                    } else {
                        clicked = true;
                        drawX = (int) (e.getX()*fullResolutionFactor);
                        drawY = (int) (e.getY()*fullResolutionFactor);
                        firstClick = true;
                        System.out.println("redrawing");
                        //isGraphDrawn = false;
                        repaint();
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e){
                zoomable = true;
                mouseReleased = true;
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {

                                   @Override
                                   public void mouseDragged(MouseEvent e) {
                                       //boolean notOutsideX = imageX + e.getX() - pressedX + 1300 <= currentResolutionX;// & imageX + e.getX() - pressedX >= 0;
                                       //boolean notOutsideY = imageY + e.getY() - pressedY + 1000 <= currentResolutionY;// & imageY + e.getY() - pressedY >= 0;
                                       //if (notOutsideX)
                                       //if (notOutsideY)
                                       //if(e.getButton() == java.awt.event.MouseEvent.BUTTON1){
                                            imageX += (pressedX - e.getX())/zoomFactor;
                                            imageY += (pressedY - e.getY())/zoomFactor;
                                       //}
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
                    zoomFactor = 0.95 * zoomFactor;
                    //zoomFactor = Math.pow(zoomFactor, 0.80)-0.05;
                    zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
                }
                else if (e.getWheelRotation() < 0) {
                    zoomFactor = 1.05 * zoomFactor +0.01;
                    //zoomFactor = Math.pow(zoomFactor, 1.20)+0.05;
                    zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
                }
                repaint();
            }
        });

    }

    public void setRouteToDraw(List<Long> path, Color color) {
        //System.out.println("Received List: "+ redPart);
        //List<String> nodeIds = Arrays.asList(redPart.split("\\s+"));
        //List<String> nodeIdsNOSPACE = nodeIds.subList(1, nodeIds.size()); //remove head of list as it is an identifier

        //Collections.addAll(path,nodeIdLongs);

        //for(String id : nodeIdsNOSPACE) {
        //    nodeIdLongs.add(Long.parseLong(id));
        //}

        //for (CustomNode node :                parser.getNodes().values()) {
        //    nodeIdLongs.add(node.getId());
        //}

        drawRoute(path, color);

        repaint();
    }

    private double scaleValueX(double x){
        return (((x)+imageX*fullResolutionFactor+viewResolution*fullResolutionFactor-viewResolution)/zoomFactor);
    }

    private double scaleValueY(double y){
        return (((y )+imageY*fullResolutionFactor+viewResolution*fullResolutionFactor-viewResolution)/zoomFactor);
    }

    private double scaleValueXNoZoom(double x){
        return (((x)*fullResolutionFactor));
    }

    private double scaleValueYNoZoom(double y){
        return (((y )*fullResolutionFactor));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        zoomFactor = Math.round(zoomFactor*1000.0)/1000.0;
        if(zoomFactor <=1 & zoomLevel!=1){
            //isGraphDrawn=false;
            zoomLevel = 1;
        }else if(zoomFactor>=1.5 & zoomFactor <= 2.5 & zoomLevel!=2){
            //isGraphDrawn=false;
            zoomLevel = 2;
        }else if(zoomFactor >=4 & zoomFactor <= 6 & zoomLevel!=3){
            //isGraphDrawn=false;
            zoomLevel = 3;
        }else if(zoomFactor >=8 & zoomFactor <= 12 & zoomLevel!=4){
            //isGraphDrawn=false;
            zoomLevel = 4;
        }else if(zoomFactor >=16 & zoomLevel!=6){
            isGraphDrawn=false;
            zoomLevel = 6;
        }

        if(!inited) {
            drawGraph(ways, prerenderedImage);
            prerenderedImage = DrawingUtil.flipYCoordinate(prerenderedImage);
            inited = true;
        }

        if(!isGraphDrawn) {

            if(zoomFactor<=1) {
                System.out.println("Changed: 100");
                //changeTilesResolution( 1);
                isGraphDrawn = true;
            }
            else if(zoomFactor>=1.5 & zoomFactor <= 2.5) {
                System.out.println("Changed: 250");
                //changeTilesResolution( 2);
                isGraphDrawn = true;
            }
            else if(zoomFactor>=4 & zoomFactor <=6) {
                System.out.println("Changed: 500");
                //changeTilesResolution( 3);
                isGraphDrawn = true;
            }
            else if(zoomFactor>=8 & zoomFactor <=14) {
                System.out.println("Changed: 1000");
                //changeTilesResolution( 4);
                isGraphDrawn = true;
            }
            else if(zoomFactor>=16) {
                System.out.println("Changed: real");
                changeTilesResolution( zoomLevel);
                isGraphDrawn = true;
            }
            System.out.println("ZoomLevel: " + zoomLevel);
            //setScopeOfImage();
            //drawBackground();
            //drawTilesBackgrounds();
            drawRoute(redPart, myColor);
            //drawGraph(ways, bufferedImage);
            //drawTiles(ways);
            //flipTiles(tiles);
            bufferedImage = DrawingUtil.flipYCoordinate(bufferedImage);
        }
        if( clicked) {
            drawRedAndBlue();
            clicked = false;
        }



        System.out.println(zoomFactor);
        System.out.println("ImageX and Y: " + imageX + "," + imageY);
        if(mouseReleased) {
            for (Point p : tiles.keySet()) {
                boolean viewResMinus100 = p.x >= imageX  & p.y >= imageY  && p.x <= imageX + (1200) / zoomFactor & p.y <= imageY + (1200) / zoomFactor;
                boolean viewRes = p.x >= imageX - tileRes & p.y >= imageY - tileRes && p.x <= imageX + (viewResolution) / zoomFactor & p.y <= imageY + (viewResolution) / zoomFactor;
                boolean viewResPlus500 = p.x >= imageX - tileRes*5 & p.y >= imageY - tileRes*5 && p.x <= imageX + (viewResolution+tileRes*5) / zoomFactor & p.y <= imageY + (viewResolution+tileRes*5) / zoomFactor;
                if (viewRes) {
                    if(zoomLevel!= 1 && zoomLevel!= 2 && zoomLevel!= 3 && zoomLevel!= 4 && tileZoom.get(p)!=zoomLevel ){
                        if(drawTiles.containsValue(p) && !drawTiles.get(p)) {
                            drawTiles.put(p, true);
                            System.out.println("I am taking so long");
                        }
                        //changeTileResolution(zoomLevel,p);
                    }
                }           else     if (viewResPlus500 ) {
                    //if(zoomLevel!= 1 && zoomLevel!= 2 && zoomLevel!= 3 &&tileZoom.get(p)!=zoomLevel){
                    //    drawTiles.put(p, false);
                    //    System.out.println("I am taking too long");
                    //    //changeTileResolution(zoomLevel,p);
                    //}
                }
                mouseReleased = false;
            }
        }
        int tilesDrawn = 0;

        if(zoomLevel == 1 || zoomLevel == 2 || zoomLevel == 3|| zoomLevel == 4) {
            g.drawImage(prerenderedImage,
                    0,
                    0,
                    1300,
                    1300,
                    (int) ((imageX * fullResolutionFactor*8)),
                    (int) ((imageY * fullResolutionFactor*8)),
                    (int) (((imageX * fullResolutionFactor*8 + viewResolution * fullResolutionFactor*8 / (zoomFactor)))),
                    (int) (((imageY * fullResolutionFactor*8 + viewResolution * fullResolutionFactor*8 / (zoomFactor)))),
                    this);
        } else {
            for (Point p : drawTiles.keySet()) {
                if(drawTiles.get(p)){
                    tilesDrawn++;
                    BufferedImage tile = tiles.get(p);
                    g.drawImage(tile,
                            (int)((p.x-imageX*fullResolutionFactor)*zoomFactor),
                            (int)((p.y-imageY*fullResolutionFactor)*zoomFactor),
                            (int)((p.x+tileRes-imageX*fullResolutionFactor)*zoomFactor),
                            (int)((p.y+tileRes-imageY*fullResolutionFactor)*zoomFactor),
                            0,
                            0,
                            tile.getWidth(),
                            tile.getHeight(),
                            this);
                }
            }
        }
        System.out.println("Tiles drawn= " + tilesDrawn);
        //g.drawImage(bufferedImage, 0, 0, 1300, 1300,
        //        (int) ((imageX    * fullResolutionFactor + zoomCalculation(zoomFactor))),
        //        (int) ((imageY    * fullResolutionFactor + zoomCalculation(zoomFactor))),
        //        (int) (((imageX   * fullResolutionFactor + viewResolution*fullResolutionFactor/ (zoomFactor)) ) ),
        //        (int) (((imageY   * fullResolutionFactor + viewResolution*fullResolutionFactor/ (zoomFactor)) ) ),
        //        this);

    }

    private void changeTilesResolution(double zoomLevel) {
        for (Point p : tilePoints) {
            if (p.x >= imageX - tileRes & p.y >= imageY - tileRes && p.x <= imageX + (viewResolution) / zoomFactor & p.y <= imageY + (viewResolution) / zoomFactor) {
                changeTileResolution(zoomLevel, p);
                tileZoom.put(p,zoomLevel);
            }
        }
    }

    private void changeTileResolution(double zoomLevel, Point p) {
        int res = (int) (Math.pow(2, zoomLevel - 1) * tileRes);
        BufferedImage biggerImage = new BufferedImage(res, res, BufferedImage.TYPE_INT_ARGB);
        tiles.put(p, biggerImage);
        drawTile(p);
    }

    private Map<Point, BufferedImage> flipTiles(Map<Point, BufferedImage> tiles) {
        for (Point p :  tilePoints) {
            BufferedImage tile = tiles.get(p);
            BufferedImage flippedTile = DrawingUtil.flipYCoordinate(tile);
            tiles.put(p,flippedTile);
        }
        return tiles;
    }

    private void drawTiles(List<CustomWay> ways) {
        for (Point p :  tilePoints) {
            drawTile(p);

        }

        isGraphDrawn = true;
    }

    private void drawTile(Point p) {
        //System.out.println("Before: " + ways.size());
        viewLimiter.setMargin(0,0);
        if(!this.tileWays.containsKey(p)) {
            List<CustomWay> limitedWays = viewLimiter.limitToRelevantWays(scaleValueXNoZoom(p.x), scaleValueYNoZoom(p.y), scaleValueXNoZoom(p.x + tileRes), scaleValueYNoZoom(p.y + tileRes), xOffset, yOffset, windowScale / fullResolutionFactor);
            tileWays.put(p,limitedWays);
        }
        //System.out.println("After: " + tileWays.size());
        BufferedImage tile = tiles.get(p);
        Graphics2D graph2d = tile.createGraphics();
        if(tileWays.size()!=0) drawTiles.put(p, true);


        for (CustomWay way: tileWays.get(p)) {
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

                    Point2D.Double prevPoint = nodeFinder.convertCoordsXYToImageXY(prevX, prevY, xOffset, yOffset, scaleFactor);
                    Point2D.Double currPoint = nodeFinder.convertCoordsXYToImageXY(currX, currY, xOffset, yOffset, scaleFactor);

                    graph2d.setStroke(new BasicStroke(1));
                    if(p.x/tileRes %2 == 0 & p.y/tileRes %2 == 0 | p.x/tileRes %2 == 1 & p.y/tileRes %2 == 1) graph2d.setStroke(new BasicStroke(1));
                    graph2d.setColor(Color.BLACK);
                    double tileResoFactor = tile.getHeight()/tileRes;
                    Shape l = new Line2D.Double((prevPoint.x- p.x)*tileResoFactor,
                            (viewResolution-prevPoint.y- p.y)*tileResoFactor,
                            (currPoint.x- p.x)*tileResoFactor,
                            (viewResolution-currPoint.y- p.y)*tileResoFactor
                    );
                    graph2d.draw(l);


                    drawCirclesOnTiles(p, tile, graph2d, Color.BLUE);
                    if(p.x/tileRes %2 == 0 & p.y/tileRes %2 == 0 | p.x/tileRes %2 == 1 & p.y/tileRes %2 == 1) { //checkered pattern
                        drawCirclesOnTiles(p, tile, graph2d, Color.GREEN);
                    }

                    previousId = currId;
                }
            } while(iterator.hasNext()); }


        graph2d.dispose();

    }

    private void drawCirclesOnTiles(Point p, BufferedImage tile, Graphics2D graph2d, Color color) {
        graph2d.setColor(color);
        graph2d.setStroke(new BasicStroke(1));
        //if(p.x/100 %2 == 0 & p.y/100 %2 == 0 | p.x/100 %2 == 1 & p.y/100 %2 == 1) { //checkered pattern
        //    graph2d.setColor(Color.GREEN);
        //    graph2d.setStroke(new BasicStroke(1));
        //}

        Shape circle = new Ellipse2D.Double(0,0, tile.getHeight(), tile.getWidth());
        graph2d.draw(circle);
    }

    //draws the red and blue circles to draw routes
    private void drawRedAndBlue() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setColor(Color.RED);
        if (!firstClick) {
            redDrawX = drawX; redDrawY = drawY;
            System.out.println("Red: " + scaleValueX(redDrawX) + "," + scaleValueY(redDrawY));
        }
        Shape red = new Ellipse2D.Double(scaleValueX(redDrawX-5), scaleValueY(redDrawY-5), 10, 10);
        graph2d.fill(red);
        graph2d.draw(red);

        graph2d.setColor(Color.BLUE);
        if (firstClick) {
            blueDrawX = drawX; blueDrawY = drawY;
            System.out.println("Blue: " + scaleValueX(blueDrawX) + "," + scaleValueY(blueDrawY));
        }
        Shape blue = new Ellipse2D.Double(scaleValueX(blueDrawX-5), scaleValueY(blueDrawY-5), 10, 10);
        graph2d.fill(blue);
        graph2d.draw(blue);

        graph2d.dispose();
        //firePropertyChange("SecoundClick", false, true);
    }

    private double zoomCalculation(double zoomFactor) {
        double res = viewResolution * fullResolutionFactor / zoomFactor;
        double diffRes = viewResolution * fullResolutionFactor - res;
        return diffRes;
    }

    private double zoomCalculationTiles(double zoomFactor) {
        double res = 100  / zoomFactor;
        double diffRes = 100  - res;
        return diffRes;
    }



    private void drawBackground() {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
        graph2d.dispose();
    }

    private void drawTilesBackgrounds() {
        for (Point p :  tilePoints) {
            drawTileBackground(p);
        }

        //drawCirclesInTiles();
    }

    private void drawTileBackground(Point p) {
        BufferedImage bufferedImage = tiles.get(p);
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
        graph2d.dispose();
    }

    private void drawCirclesInTiles() {
        for (Point p :  tilePoints) {
            BufferedImage bufferedImage = tiles.get(p);
            Graphics2D graph2d = bufferedImage.createGraphics();
            BufferedImage tile = tiles.get(p);

            graph2d.setStroke(new BasicStroke(15));
            graph2d.setColor(Color.BLUE);
            Shape rec = new Ellipse2D.Double(0,0,tile.getHeight(), tile.getWidth());
            graph2d.draw(rec);
            graph2d.dispose();
        }
    }

    private void drawRoute(List<Long> route, Color color) {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(15));
        int alpha = 80; // 50% transparent
        Color myColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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

    private void drawGraph(List<CustomWay> ways, BufferedImage bufferedImage) {
        Graphics2D graph2d = bufferedImage.createGraphics();
        int fullResolutionFactor = this.fullResolutionFactor*8;
        graph2d.setStroke(new BasicStroke(1));
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

                    Point2D.Double prevPoint = nodeFinder.convertCoordsXYToImageXY(prevX, prevY, xOffset, yOffset, scaleFactor);
                    Point2D.Double currPoint = nodeFinder.convertCoordsXYToImageXY(currX, currY, xOffset, yOffset, scaleFactor);

                    Shape l = new Line2D.Double(prevPoint.x,
                                                prevPoint.y,
                                                currPoint.x,
                                                currPoint.y);
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }


        graph2d.dispose();
    }

    private void drawGraphTiles(List<CustomWay> ways, BufferedImage bufferedImage) {
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(15));
        graph2d.setColor(Color.BLACK);


        isGraphDrawn = true;
    }

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }


    public String getFrom() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint(scaleValueX(redDrawX), scaleValueY(redDrawY), parser.getNodes(), xOffset, yOffset, windowScale /fullResolutionFactor));
    }
    public String getTo() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint(scaleValueX(blueDrawX), scaleValueY(blueDrawY), parser.getNodes(), xOffset, yOffset, windowScale /fullResolutionFactor));
    }
}
































