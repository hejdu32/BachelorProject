package XMLParser;
import crosby.binary.osmosis.OsmosisReader;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.container.v0_6.RelationContainer;
import org.openstreetmap.osmosis.core.container.v0_6.WayContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.Map;

public class MyOsmReader implements Sink{
    @Override
    public void process(EntityContainer entityContainer) {
        if (entityContainer instanceof NodeContainer){
            Node node1 =((NodeContainer) entityContainer).getEntity();
            if(node1.getId() == 22789888){
                System.out.println(node1);
                System.out.println(node1.getLatitude());
                System.out.println(node1.getLongitude());
                System.out.println(node1.getUser());
                System.out.println(node1.toString());
            }
        }

    }

    @Override
    public void initialize(Map<String, Object> metaData) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void release() {

    }
}
