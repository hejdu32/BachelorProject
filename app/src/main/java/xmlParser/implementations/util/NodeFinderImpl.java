package xmlParser.implementations.util;

import xmlParser.framework.NodeFinder;
import xmlParser.framework.XMLParser;
import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.Edge;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeFinderImpl implements NodeFinder {
    @Override
    public long findClosestNodeToPoint(double x, double y, Map<Long, CustomNode> nodes, int xOffset, int yOffset, double scaleFactor, double routeFactor) {
        long resultNode = 0;
        double actualX = 0;
        double actualY = 0;
        Iterable<CustomNode> ListOfNodes = nodes.values();
        double bestSoFar = Integer.MAX_VALUE;
        for (CustomNode node:ListOfNodes) {
            actualX = node.getLatitudeAsXCoord();
            actualY = node.getLongtitudeAsYCoord();

            double diffX = Math.abs(x-((actualX-xOffset)/scaleFactor));
            double diffY = Math.abs(Math.abs(y-1300*routeFactor)-((actualY-yOffset)/scaleFactor));
            if( diffX + diffY <= bestSoFar) {
                bestSoFar = diffX+diffY;

                resultNode = node.getId();
            }
        }
        //System.out.println(resultNode + " @ " + nodes.get(resultNode).getLatitudeAsXCoord() + ", " + nodes.get(resultNode).getLongtitudeAsYCoord()); //convertCoordsXYToImageXY(, , xOffset, yOffset, scaleFactor));
        return resultNode;
    }

    public Point2D.Double convertCoordsXYToImageXY(double x, double y, int xOffset, int yOffset, double scaleFactor) {
        double imageX=0;
        double imageY=0;
        imageX = ((x-xOffset)/scaleFactor);
        imageY = ((y-yOffset)/scaleFactor);
        return new Point2D.Double(imageX,imageY);
    }


    public java.util.List<Long> findClosestReducedNodes(long node, XMLParser parser, HashMap<Long, List<Edge>> reducedList) {
        List<Long> result = new ArrayList<>();
        CustomWay currWay = null;

        for (CustomWay way : parser.getWays().values()) {
            if(way.getNodeIdList().contains(node)) currWay = way;
        }
        if(currWay==null) System.out.println("currWay is null");

        if(currWay.isOneWay() != null && currWay.isOneWay().equals("1")){ //if oneway move back through list of node in way
            for (int i = currWay.getNodeIdList().indexOf(node); i > 0; i--) {
                Long elem = currWay.getNodeIdList().get(i);
                boolean existsInReducedList = false;
                if(reducedList != null && elem != null)
                    existsInReducedList = reducedList.keySet().contains(elem);
                if (existsInReducedList) {
                    result.add(elem);
                    break;
                }
            }
        }
        else { //two-way, find two closest nodes
            //find first
            for (int i = currWay.getNodeIdList().indexOf(node); i >= 0; i--) {
                Long elem = currWay.getNodeIdList().get(i);
                boolean existsInReducedList = false;
                if(reducedList != null && elem != null)
                    existsInReducedList = reducedList.keySet().contains(elem);
                if (existsInReducedList) {
                    result.add(elem);
                    break;
                }
            }
            //find second
            for (int i = currWay.getNodeIdList().indexOf(node); i < currWay.getNodeIdList().size(); i++) {
                Long elem = currWay.getNodeIdList().get(i);
                boolean existsInReducedList = false;
                if(reducedList != null && elem != null)
                    existsInReducedList = reducedList.keySet().contains(elem);
                if (existsInReducedList) {
                    result.add(elem);
                    break;
                }
            }
        }
        if(result.size()==0) System.out.println("I AM A USELESS FUCKING METHOD THAT DOES NOTHING");
        return result;
    }

}
