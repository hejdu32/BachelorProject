package xmlParser.implementations;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.container.v0_6.NodeContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;

import java.util.Map;

public class SecondPassSink implements Sink {

    private final XMLParser parser;

    public SecondPassSink(XMLParser parser){
        this.parser = parser;
    }
    @Override
    public void process(EntityContainer entityContainer) {
        if(entityContainer instanceof NodeContainer) {
            Node node1 =((NodeContainer) entityContainer).getEntity();
            long nodeId = node1.getId();
            if(parser.getNodesToSearchFor().contains(nodeId)){
                parser.getNodes().put(nodeId, new MyNode(nodeId, node1.getLongitude(), node1.getLatitude()));
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

    public XMLParser getParser() {
        return parser;
    }

}
