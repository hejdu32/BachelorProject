package xmlParser.implementations.visualization;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MapAdapter extends MouseAdapter {

    private GraphOfNodes graphOfNodes;
    private Point pointPressed;
    private Point pointReleased;

    public MapAdapter(GraphOfNodes graphOfNodes) {
        super();
        //this.graphOfNodes = graphOfNodes;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pointPressed = e.getPoint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pointReleased = e.getPoint();
        graphOfNodes.setImageX(pointReleased.x - pointPressed.x);
        graphOfNodes.setImageY(pointReleased.y - pointPressed.y);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
