package xmlParser.implementations.testImplementation;

import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import java.util.*;

public class XMLParserStump extends XMLParserImpl {
    private List<CustomWay> ways;
    private Map<Long, CustomNode> nodes;

    public XMLParserStump(){
        this.nodes = new HashMap<>();
        CustomNode a = new CustomNode(1L, 1.0, 1.0);
        CustomNode b = new CustomNode(2L, 2.0, 1.0);
        CustomNode c = new CustomNode(3L, 3.0, 1.0);
        CustomNode d = new CustomNode(4L, 2.0, 2.0);
        CustomNode e = new CustomNode(5L, 2.0, 12.0);
        nodes.put(1L, a); nodes.put(2L, b); nodes.put(3L, c); nodes.put(4L, d); nodes.put(5L, e);
        this.ways = new ArrayList<>();
        CustomWay abc = new CustomWay(6L, new ArrayList<Long>(Arrays.asList(1L, 2L, 3L)), "");
        CustomWay bde = new CustomWay(7L, new ArrayList<Long>(Arrays.asList(2L, 4L, 5L)), "");
        this.ways.add(abc); ways.add(bde);
    }

    @Override
    public List<CustomWay> getWays() {
        return ways;
    }
    @Override
    public Map<Long, CustomNode> getNodes() {
        return nodes;
    }
}
