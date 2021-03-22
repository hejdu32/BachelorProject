package xmlParser.implementations.visualization;


import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
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
    private Graphics2D graph2d;
    private BufferedImage bufferedImage;
    private int imageX = 0;
    private int imageY = 0;
    private boolean isGraphDrawn = false;
    private int pressedX;
    private int pressedY;
    private double zoomFactor = 1.0;
    private int zoomOffSetX = 0;
    private int zoomOffSetY = 0;
    private boolean zoomable;
    private BufferedImage scaledImage;
    private static int fullResolutionFactor = 4;
    private static int fullResolutionX = 1300 * fullResolutionFactor;
    private static int fullResolutionY = 1000 * fullResolutionFactor;
    private static int currentResolutionX = fullResolutionX;
    private static int currentResolutionY = fullResolutionY;

    public GraphOfNodes(XMLParserImpl parser) {
        this.parser = parser;
        this.bufferedImage = new BufferedImage(fullResolutionX,fullResolutionY,BufferedImage.TYPE_BYTE_BINARY);

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
                isGraphDrawn = true;
                zoomable = false;
            }
            @Override
            public void mouseReleased(MouseEvent e){
                isGraphDrawn = false;
                zoomable = true;
            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                boolean notOutsideX = imageX + e.getX() - pressedX + 1300 <= currentResolutionX & imageX + e.getX() - pressedX >= 0;
                boolean notOutsideY = imageY + e.getY() - pressedY + 1000 <= currentResolutionY & imageY + e.getY() - pressedY >= 0;
                if (notOutsideX) imageX += e.getX() - pressedX;
                if (notOutsideY) imageY += e.getY() - pressedY;
                pressedX = e.getX();
                pressedY = e.getY();
                repaint();
            }
        });
        addMouseWheelListener(new MouseAdapter() {
            //Broken zoom stuff
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoomOffSetX = (int) (zoomFactor * 1.2);
                zoomFactor = Math.round(zoomFactor*100.0)/100.0;
                zoomOffSetY = (int) (zoomFactor * 1.2);
                zoomFactor = Math.round(zoomFactor*100.0)/100.0;
                if(e.getWheelRotation() > 0) {
                    zoomFactor *= 1.2;
                    zoomFactor = Math.round(zoomFactor*100.0)/100.0;
                    drawGraph();
                }
                else if (e.getWheelRotation() < 0) {
                    zoomFactor *= 0.8;
                    zoomFactor = Math.round(zoomFactor*100.0)/100.0;
                    drawGraph();
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
            scaledImage = bufferedImage;
        }
        if (zoomable) {
            BufferedImage afterImage = bufferedImage;
            afterImage = scale2(bufferedImage, zoomFactor);
            scaledImage = afterImage;
            zoomFactor = Math.round(zoomFactor*100.0)/100.0;
            System.out.println("Scale : " + zoomFactor);
        }
        int zoomResolutionX = (int) (1300 / zoomFactor);
        int zoomResolutionY = (int) (1000 / zoomFactor);
        BufferedImage subImage = scaledImage.getSubimage(imageX, imageY, 1300, 1000);

        //BufferedImage windowsSizedImage = resizeImage(subImage, 1300, 1000);

        System.out.println("imageX: " + imageX + " imageY: " + imageY);
        g.drawImage(subImage,0,0,this);
    }

    private void flipYCoordinate() {
        AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
    }

    private BufferedImage scaleImage(BufferedImage bi, double zoomFactor) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(zoomFactor, zoomFactor);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return scaleOp.filter(bi, after);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight){
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_BINARY);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    private static BufferedImage scale2(BufferedImage before, double scale) {
        int w = fullResolutionX;
        int h = fullResolutionY;
        // Create a new image of the proper size
        int w2 = (int) (w * scale);
        int h2 = (int) (h * scale);
        currentResolutionX = w2;
        currentResolutionY = h2;
        BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp
                = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

        Graphics2D g2 = (Graphics2D) after.getGraphics();
        // Here, you may draw anything you want into the new image, but we're
        // drawing a scaled version of the original image.
        g2.drawImage(before, scaleOp, 0, 0);
        g2.dispose();
        return after;
    }

    private void drawBackground() {
        graph2d = bufferedImage.createGraphics();
        graph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        graph2d.setColor(Color.WHITE);
        graph2d.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
    }


    private void drawGraph() {
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
////
        //            CustomNode currNode = parser.getNodes().get(currId);
        //            double currX = currNode.getLatitudeAsXCoord();
        //            double currY = currNode.getLongtitudeAsYCoord();
////
        //            int scaleFactor = (int) (380/ fullResolutionFactor);
        //            int yOffset = 6049800;
        //            int xOffset = 441800;
        //            Shape l = new Line2D.Double(((prevX-xOffset)/ scaleFactor) , ((prevY-yOffset)/ scaleFactor) , ((currX-xOffset)/ scaleFactor) , ((currY-yOffset)/ scaleFactor) );
        //            graph2d.draw(l);
        //            previousId = currId;
        //        }
        //    } while(iterator.hasNext()); }

        //graph2d.dispose();

        File img = new File("C:/Users/svend/OneDrive/Uni/6. semester/Bachelor Project/thumb-1920-999633.jpg");
        try {
            bufferedImage = ImageIO.read(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Shape l = new Rectangle2D.Double(100,100,4000,4000);
        graph2d.draw(l);
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
































