package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.Edge;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;


public class GraphOfNodes extends JPanel{


    private final XMLParserImpl parser;
    private final ViewLimiterImpl viewLimiter;
    private final NodeFinderImpl nodeFinder;
    private BufferedImage ballImage;
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
    private Color seenColor = Color.orange;
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
    private int routeFactor = 8;
    private boolean drawBlue = false;
    private int redDrawAtPointX;
    private int redDrawAtPointY;
    private int blueDrawAtPointX;
    private int blueDrawAtPointY;
    private long chosenLandmark = -1;
    private HashMap<Long, List<Edge>> reducedAdjList = null;
    private HashMap<Long, List<Long>> from = new HashMap<>();
    private HashMap<Long, List<Long>> to = new HashMap<>();
    private final int roadWidth = 1;
    private HashMap<Long, List<Edge>> adjacencyList;
    private HashSet<Long> seenWaysToDraw = new HashSet<>();


    /*
    * Contructor
    *
    * */
    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        ways = new ArrayList<>(parser.getWays().values());
        this.bufferedImage = new BufferedImage(fullResolutionX*routeFactor,fullResolutionY*routeFactor,BufferedImage.TYPE_INT_ARGB);
        this.ballImage = new BufferedImage(fullResolutionX*(routeFactor),fullResolutionY*(routeFactor),BufferedImage.TYPE_INT_ARGB);
        //this.consideredImage = new BufferedImage(fullResolutionX*routeFactor,fullResolutionY*routeFactor,BufferedImage.TYPE_INT_ARGB);
        this.prerenderedImage = new BufferedImage(fullResolutionX*routeFactor,fullResolutionY*routeFactor,BufferedImage.TYPE_INT_ARGB);
        viewLimiter = new ViewLimiterImpl(ways, parser.getNodes());
        nodeFinder = new NodeFinderImpl();
        setBackground(Color.WHITE);
        for (int i = 0; i<viewResolution; i=i+100){
            for (int j = 0; j<viewResolution; j=j+100){
                Point point = new Point(i,j);
                tilePoints.add(point);
                tiles.put(point, new BufferedImage(tileRes,tileRes, BufferedImage.TYPE_INT_ARGB));
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
                if(e.getButton() == java.awt.event.MouseEvent.BUTTON3){
                    if (firstClick) {
                        clicked = true;
                        drawX = (int) (e.getX()*fullResolutionFactor);
                        drawY = (int) (e.getY()*fullResolutionFactor);
                        firstClick = false;
                        //System.out.println("redrawing");
                        //isGraphDrawn = false;
                        repaint();
                    } else {
                        clicked = true;
                        drawX = (int) (e.getX()*fullResolutionFactor);
                        drawY = (int) (e.getY()*fullResolutionFactor);
                        firstClick = true;
                        //System.out.println("redrawing");
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

        this.bufferedImage = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);

        //drawRoute(path, color);

        redPart = path;
        myColor = color;
        isGraphDrawn = false;
    }

    public void setWaysToDraw(List<Long> nodes, Color color) {

        seenWaysToDraw = new HashSet<Long>(nodes);
        seenColor = color;
        //System.out.println("size of nodes" +nodes.size());
        //HashSet<Long> seenSet = new HashSet<Long>(nodes);
        //drawSeenWays(seenSet, color);
        isGraphDrawn = false;

        repaint();
    }

    private double scaleValueX(double x){
        return (((x*fullResolutionFactor*routeFactor)/zoomFactor+imageX*fullResolutionFactor*routeFactor));
    }

    private double scaleValueY(double y){
        return (((y*fullResolutionFactor*routeFactor)/zoomFactor+imageY*fullResolutionFactor*routeFactor));
    }

    private double scaleValueXNoZoom(double x){
        return (((x)*fullResolutionFactor*routeFactor));
    }

    private double scaleValueYNoZoom(double y){
        return (((y )*fullResolutionFactor*routeFactor));
    }

    @Override            //#############################################################################################################
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
            this.bufferedImage = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_ARGB);

            if(zoomFactor<=1) {
                //System.out.println("Changed: 100");
                //changeTilesResolution( 1);
                //isGraphDrawn = true;
            }
            else if(zoomFactor>=1.5 & zoomFactor <= 2.5) {
                //System.out.println("Changed: 250");
                //changeTilesResolution( 2);
                //isGraphDrawn = true;
            }
            else if(zoomFactor>=4 & zoomFactor <=6) {
                //System.out.println("Changed: 500");
                //changeTilesResolution( 3);
                //isGraphDrawn = true;
            }
            else if(zoomFactor>=8 & zoomFactor <=14) {
                //System.out.println("Changed: 1000");
                //changeTilesResolution( 4);
                isGraphDrawn = true;
            }
            else if(zoomFactor>=16) {
                //System.out.println("Changed: real");
                Instant start = Instant.now();
                changeTilesResolution( zoomLevel);
                Instant end = Instant.now();
                Duration timeElapsed = Duration.between(start, end);
                //System.out.println("Time taken: "+ timeElapsed.toSeconds() +"." + timeElapsed.toMillisPart() + " seconds" );
                isGraphDrawn = true;
            }
            //System.out.println("ZoomLevel: " + zoomLevel);
            //setScopeOfImage();
            //drawTilesBackgrounds();
            if(adjacencyList!=null) drawAdjList(adjacencyList, Color.red);
            if(redPart!=null) drawRoute(redPart, myColor);
            if(seenWaysToDraw!=null) drawSeenWays(seenWaysToDraw,seenColor);
            if(chosenLandmark!=-1) drawLandmark(chosenLandmark);
            //drawSeenNodes(testList,myColor);
            //drawGraph(ways, bufferedImage);
            //drawTiles(ways);
            //flipTiles(tiles);
            isGraphDrawn = true;
            bufferedImage = DrawingUtil.flipYCoordinate(bufferedImage);
        }
        if( clicked) {
            drawStartAndTarget();
            //ballImage = DrawingUtil.flipYCoordinate(ballImage);
            clicked = false;
        }

        int tilesDrawn = 0;


        g.drawImage(bufferedImage,
                0,
                0,
                1300,
                1300,
                (int) ((imageX * fullResolutionFactor   *routeFactor)),
                (int) ((imageY * fullResolutionFactor   *routeFactor)),
                (int) (((imageX * fullResolutionFactor  *routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
                (int) (((imageY * fullResolutionFactor  *routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
                this);

        if(zoomLevel == 1 || zoomLevel == 2 || zoomLevel == 3|| zoomLevel == 4) {
            g.drawImage(prerenderedImage,
                    0,
                    0,
                    1300,
                    1300,
                    (int) ((imageX * fullResolutionFactor*routeFactor)),
                    (int) ((imageY * fullResolutionFactor*routeFactor)),
                    (int) (((imageX * fullResolutionFactor*routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
                    (int) (((imageY * fullResolutionFactor*routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
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



        g.drawImage(ballImage,
                0,
                0,
                1300,
                1300,
                (int) ((imageX * fullResolutionFactor   *routeFactor)),
                (int) ((imageY * fullResolutionFactor   *routeFactor)),
                (int) (((imageX * fullResolutionFactor  *routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
                (int) (((imageY * fullResolutionFactor  *routeFactor + viewResolution * fullResolutionFactor*routeFactor / (zoomFactor)))),
                this);

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
            List<CustomWay> limitedWays = viewLimiter.limitToRelevantWays(scaleValueXNoZoom(p.x),
                    scaleValueYNoZoom(p.y),
                    scaleValueXNoZoom(p.x + tileRes),
                    scaleValueYNoZoom(p.y + tileRes),
                    xOffset,
                    yOffset,
                    windowScale / (fullResolutionFactor*routeFactor));
            System.out.println("Point: " + p + " ways: " + limitedWays.size());
            tileWays.put(p,limitedWays);
        }
        System.out.println("After: " + tileWays.get(p).size());
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
                    //if(p.x/tileRes %2 == 0 & p.y/tileRes %2 == 0 | p.x/tileRes %2 == 1 & p.y/tileRes %2 == 1) graph2d.setStroke(new BasicStroke(1));
                    graph2d.setColor(Color.BLACK);
                    double tileResoFactor = tile.getHeight()/tileRes;
                    Shape l = new Line2D.Double((prevPoint.x- p.x)*tileResoFactor,
                            (viewResolution-prevPoint.y- p.y)*tileResoFactor,
                            (currPoint.x- p.x)*tileResoFactor,
                            (viewResolution-currPoint.y- p.y)*tileResoFactor
                    );
                    graph2d.draw(l);


                    //drawCirclesOnTiles(p, tile, graph2d, Color.BLUE);
                    //if(p.x/tileRes %2 == 0 & p.y/tileRes %2 == 0 | p.x/tileRes %2 == 1 & p.y/tileRes %2 == 1) { //checkered pattern
                    //    drawCirclesOnTiles(p, tile, graph2d, Color.GREEN);
                    //}

                    previousId = currId;
                }
            } while(iterator.hasNext()); }


        graph2d.dispose();

    }

    //draws the red and blue circles to draw routes
    private void drawStartAndTarget() {
        Graphics2D graph2d = ballImage.createGraphics();
        graph2d.setColor(Color.GREEN);
        if (!firstClick) {
            //drawBackground(ballImage);
            graph2d.dispose();
            this.ballImage = new BufferedImage(ballImage.getWidth(),ballImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
            graph2d = ballImage.createGraphics();
            graph2d.setColor(Color.GREEN);
            if(redDrawX!=drawX && redDrawY !=drawY){
                redDrawX = drawX; redDrawY = drawY;
                Point2D pointToDrawAt = findClosestNodePoint(redDrawX, redDrawY);
                redDrawAtPointX = (int) pointToDrawAt.getX();
                redDrawAtPointY = (int) Math.abs(pointToDrawAt.getY()-ballImage.getHeight());
                //redDrawAtPointY = (int) pointToDrawAt.getY();
                System.out.println("Red: " + scaleValueX(redDrawX) + "," + scaleValueY(redDrawY));
                drawBlue=false;
                firePropertyChange("red", false, true);
            //System.out.println("NodeFound: " + String.valueOf(nodeFinder.findClosestNodeToPoint(pointToDrawAt.getX(), pointToDrawAt.getY(), parser.getNodes(), xOffset, yOffset, windowScale /routeFactor)));
            }
        }
        Shape red = new Ellipse2D.Double(redDrawAtPointX, redDrawAtPointY, 10*routeFactor, 10*routeFactor);
        Shape redSquare = new Rectangle2D.Double(redDrawAtPointX, redDrawAtPointY, 10*routeFactor/2, 10*routeFactor/2);
        graph2d.fill(red);
        graph2d.draw(red);
        graph2d.fill(redSquare);
        graph2d.draw(redSquare);

        graph2d.setColor(Color.RED);
        if (firstClick) {
            if(blueDrawX!=drawX && blueDrawY !=drawY) {
                blueDrawX = drawX;
                blueDrawY = drawY;
            }
            System.out.println("Blue: " + scaleValueX(blueDrawX) + "," + scaleValueY(blueDrawY));
            drawBlue = true;
        }
        if(drawBlue) {
            Point2D pointToDrawAt = findClosestNodePoint(blueDrawX, blueDrawY);
            blueDrawAtPointX = (int) pointToDrawAt.getX();
            blueDrawAtPointY = (int) Math.abs(pointToDrawAt.getY()-ballImage.getHeight());
            //blueDrawAtPointY = (int) pointToDrawAt.getY();
            Shape blue = new Ellipse2D.Double(blueDrawAtPointX, blueDrawAtPointY, 10*routeFactor, 10*routeFactor);
            Shape blueSquare = new Rectangle2D.Double(blueDrawAtPointX, blueDrawAtPointY, 10*routeFactor/2, 10*routeFactor/2);
            graph2d.fill(blue);
            graph2d.draw(blue);
            firePropertyChange("blue", false, true);

            //System.out.println("NodeFound: " + String.valueOf(nodeFinder.findClosestNodeToPoint(pointToDrawAt.getX(), pointToDrawAt.getY(), parser.getNodes(), xOffset, yOffset, windowScale /routeFactor)));

            //firePropertyChange("SecoundClick", false, true);
        }

        graph2d.dispose();
    }

    private Point2D findClosestNodePoint(int drawX, int drawY) {
        long node = nodeFinder.findClosestNodeToPoint(scaleValueX(drawX), scaleValueY(drawY), parser.getNodes(), xOffset, yOffset, windowScale / (fullResolutionFactor * routeFactor), routeFactor);
        return nodeFinder.convertCoordsXYToImageXY(
                parser.getNodes().get(node).getLatitudeAsXCoord(),
                parser.getNodes().get(node).getLongtitudeAsYCoord(),
                xOffset, yOffset, windowScale / (fullResolutionFactor * routeFactor));
    }

    private void drawBackground(BufferedImage image) {
        Graphics2D graph2d = image.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,image.getWidth(),image.getHeight());
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

    private void drawRoute(List<Long> route, Color color) {
        int fullResolutionFactor = this.fullResolutionFactor*routeFactor;
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(roadWidth*4*routeFactor));
        int alpha = 255; // 50% transparent
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
                            //Math.abs(((prevY-yOffset)/ scaleFactor)-fullResolutionY*routeFactor),
                            (((prevY-yOffset)/ scaleFactor)),
                            ((currX-xOffset)/ scaleFactor),
                            //Math.abs(((currY-yOffset)/ scaleFactor)-fullResolutionY*routeFactor) );
                            (((currY-yOffset)/ scaleFactor)));
                    graph2d.draw(l);
                    previousId = currId;
                }
            } while(iterator.hasNext()); }
        graph2d.dispose();
    }

    private void drawSeenWays(HashSet<Long> nodes, Color color) {
        int fullResolutionFactor = this.fullResolutionFactor*routeFactor;
        //this.consideredImage = new BufferedImage(consideredImage.getWidth(),consideredImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(roadWidth*2));
        int alpha = 255; // 0% transparent
        Color myColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        graph2d.setColor(myColour);
        for (CustomWay way: ways) {
            long previousId = 0L;
            Iterator iterator = way.getNodeIdList().iterator();
            do {
                long currId = (long) iterator.next();
                //if(adjacencyList != null && nodes.contains(currId) &&  adjacencyList.get(currId) != null && adjacencyList.get(currId).size()!=0) {
                    if (previousId == 0L) {
                        previousId = currId;
                    } else {
                        if (nodes.contains(currId) && nodes.contains(previousId)) {
                            CustomNode previousNode = parser.getNodes().get(previousId);
                            double prevX = previousNode.getLatitudeAsXCoord();
                            double prevY = previousNode.getLongtitudeAsYCoord();

                            CustomNode currNode = parser.getNodes().get(currId);
                            double currX = currNode.getLatitudeAsXCoord();
                            double currY = currNode.getLongtitudeAsYCoord();

                            double scaleFactor = (windowScale / fullResolutionFactor);

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
                    //}
                }
            } while(iterator.hasNext()); }

        System.out.println("Done with drawing");
        graph2d.dispose();
    }

    public void drawAdjList(HashMap<Long, List<Edge>> adjacencyList, Color color) {
        System.out.println("Drawing adjList");
        //this.bufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int fullResolutionFactor = this.fullResolutionFactor*routeFactor;
        Graphics2D graph2d = bufferedImage.createGraphics();

        graph2d.setStroke(new BasicStroke(1));
        int alpha = 255; // 50% transparent
        Color myColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        graph2d.setColor(myColour);

        for (Long id : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(id)) {
                Long start = id;
                long dest = edge.getDestinationId();

                CustomNode previousNode = parser.getNodes().get(start);
                double prevX = previousNode.getLatitudeAsXCoord();
                double prevY = previousNode.getLongtitudeAsYCoord();

                CustomNode currNode = parser.getNodes().get(dest);
                double currX = currNode.getLatitudeAsXCoord();
                double currY = currNode.getLongtitudeAsYCoord();

                double scaleFactor = (windowScale / fullResolutionFactor);

                int yOffset = (int) viewLimiter.getLowestY();
                int xOffset = (int) viewLimiter.getLowestX();

                Point2D.Double prevPoint = nodeFinder.convertCoordsXYToImageXY(prevX, prevY, xOffset, yOffset, scaleFactor);
                Point2D.Double currPoint = nodeFinder.convertCoordsXYToImageXY(currX, currY, xOffset, yOffset, scaleFactor);

                Shape l = new Line2D.Double(prevPoint.x,
                        prevPoint.y,
                        currPoint.x,
                        currPoint.y);
                graph2d.draw(l);
            }

        }
        graph2d.dispose();
    }

    private void drawSeenNodes(List<Long> nodes, Color color) {
        int fullResolutionFactor = this.fullResolutionFactor*routeFactor;
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(2));
        int alpha = 40; // 50% transparent
        Color myColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        graph2d.setColor(myColour);
        for (Long id : nodes){
            CustomNode node = parser.getNodes().get(id);
            double nodeX = node.getLatitudeAsXCoord();
            double nodeY = node.getLongtitudeAsYCoord();

            double scaleFactor =  (windowScale / fullResolutionFactor);

            int yOffset = (int) viewLimiter.getLowestY();
            int xOffset = (int) viewLimiter.getLowestX();

            Point2D.Double nodePoint = nodeFinder.convertCoordsXYToImageXY(nodeX, nodeY, xOffset, yOffset, scaleFactor);

            Shape mark = new Ellipse2D.Double(nodePoint.x, nodePoint.y, 1, 1);
            graph2d.draw(mark);
        }
        graph2d.dispose();
        repaint();
    }

    private void drawGraph(List<CustomWay> ways, BufferedImage bufferedImage) {
        Graphics2D graph2d = bufferedImage.createGraphics();
        int fullResolutionFactor = this.routeFactor;
        graph2d.setStroke(new BasicStroke(roadWidth));
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

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }

    public void drawReducedGraph() {
        //drawSeenWays(new ArrayList<>(adjencencyList.keySet()), Color.red);
        repaint();
    }

    public List<Long> getFromNodes() {
        long nodeFrom = nodeFinder.findClosestNodeToPoint(scaleValueX(redDrawX),
                scaleValueY(redDrawY),
                parser.getNodes(),
                xOffset,
                yOffset,
                windowScale / (fullResolutionFactor * routeFactor), routeFactor);
        List<Long> closestNodes = nodeFinder.findClosestReducedNodes(nodeFrom, parser, reducedAdjList);
        from.put(nodeFrom, closestNodes);
        return closestNodes;
    }
    public List<Long> getToNodes() {
        long nodeTo = nodeFinder.findClosestNodeToPoint(scaleValueX(blueDrawX),
                scaleValueY(blueDrawY),
                parser.getNodes(),
                xOffset,
                yOffset,
                windowScale /(fullResolutionFactor*routeFactor), routeFactor);
        List<Long> closestNodes = nodeFinder.findClosestReducedNodes(nodeTo, parser, reducedAdjList);
        to.put(nodeTo, closestNodes);
        return closestNodes;
    }

    public HashMap<Long, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public String getFrom() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint(scaleValueX(redDrawX), scaleValueY(redDrawY), parser.getNodes(), xOffset, yOffset, windowScale / (fullResolutionFactor * routeFactor), routeFactor));
    }

    public void drawStart(Point2D pointToDrawAt) {
        Graphics2D graph2d = ballImage.createGraphics();
        graph2d.dispose();
        this.ballImage = new BufferedImage(ballImage.getWidth(),ballImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        graph2d = ballImage.createGraphics();
        graph2d.setColor(Color.GREEN);
        Point2D.Double redPoint = nodeFinder.convertCoordsXYToImageXY(pointToDrawAt.getX(), pointToDrawAt.getY(), xOffset, yOffset, windowScale / (fullResolutionFactor * (routeFactor)));
        redDrawAtPointX = (int) redPoint.x;
        redDrawAtPointY = (int) Math.abs(redPoint.y-1300*routeFactor);
        System.out.println("Drawing red at " + redDrawAtPointX + ", "+ redDrawAtPointY);
        Shape red = new Ellipse2D.Double(redDrawAtPointX, redDrawAtPointY, 10*routeFactor, 10*routeFactor);
        Shape redSquare = new Rectangle2D.Double(redDrawAtPointX, redDrawAtPointY, 10*routeFactor/2, 10*routeFactor/2);
        graph2d.fill(red);
        graph2d.draw(red);
        graph2d.fill(redSquare);
        graph2d.draw(redSquare);
        graph2d.dispose();
    }
    public void drawTarget(Point2D pointToDrawAt) {
        Graphics2D graph2d = ballImage.createGraphics();
        graph2d.setColor(Color.RED);
        Point2D.Double bluePoint = nodeFinder.convertCoordsXYToImageXY(pointToDrawAt.getX(), pointToDrawAt.getY(), xOffset, yOffset, windowScale / (fullResolutionFactor * (routeFactor)));
        blueDrawAtPointX = (int) bluePoint.x;
        blueDrawAtPointY = (int) Math.abs(bluePoint.y-1300*routeFactor);
        System.out.println("Drawing green at " + blueDrawAtPointX + ", "+ blueDrawAtPointY);
        Shape blue = new Ellipse2D.Double(blueDrawAtPointX, blueDrawAtPointY, 10*routeFactor, 10*routeFactor);
        Shape blueSquare = new Rectangle2D.Double(blueDrawAtPointX, blueDrawAtPointY, 10*routeFactor/2, 10*routeFactor/2);
        graph2d.fill(blue);
        graph2d.draw(blue);
        graph2d.fill(blueSquare);
        graph2d.draw(blueSquare);
        graph2d.dispose();
    }

    public String getTo() {
        return String.valueOf(nodeFinder.findClosestNodeToPoint(scaleValueX(blueDrawX), scaleValueY(blueDrawY), parser.getNodes(), xOffset, yOffset, windowScale /(fullResolutionFactor*routeFactor), routeFactor));
    }


    public void setAdjacencyList(HashMap<Long, List<Edge>> adjacencyList) {
        System.out.println("AdjList set!");
        this.adjacencyList = adjacencyList;
        isGraphDrawn = false;
    }

    public HashMap<Long, List<Edge>> getReducedAdjList() {
        return reducedAdjList;
    }

    public void setReducedAdjList(HashMap<Long, List<Edge>> reducedAdjList) {
        this.reducedAdjList = reducedAdjList;
    }

    public void drawLandmark(long chosenLandmark) {
        System.out.println("Drawing landmark: " + chosenLandmark);
        int fullResolutionFactor = this.fullResolutionFactor*routeFactor;
        Graphics2D graph2d = bufferedImage.createGraphics();
        graph2d.setStroke(new BasicStroke(10));
        Color myColour = new Color(255, 0, 0, 255);
        graph2d.setColor(myColour);
        CustomNode node = parser.getNodes().get(chosenLandmark);
        double nodeX = node.getLatitudeAsXCoord();
        double nodeY = node.getLongtitudeAsYCoord();
        double scaleFactor =  (windowScale / fullResolutionFactor);
        int yOffset = (int) viewLimiter.getLowestY();
        int xOffset = (int) viewLimiter.getLowestX();
        Point2D.Double nodePoint = nodeFinder.convertCoordsXYToImageXY(nodeX, nodeY, xOffset, yOffset, scaleFactor);
        Shape mark = new Ellipse2D.Double(nodePoint.x-50, nodePoint.y-50, 100, 100);
        graph2d.draw(mark);
        Shape mark2 = new Ellipse2D.Double(nodePoint.x, nodePoint.y, 1, 1);
        graph2d.draw(mark2);
        graph2d.dispose();
    }

    public void setLandmark(long chosenLandmark) {
        this.chosenLandmark = chosenLandmark;
        isGraphDrawn = false;
    }
}
































