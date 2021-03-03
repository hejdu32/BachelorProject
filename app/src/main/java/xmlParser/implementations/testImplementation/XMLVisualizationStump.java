package xmlParser.implementations.testImplementation;

import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;
import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.parsing.XMLParserImpl;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.*;

public class XMLVisualizationStump extends XMLParserImpl {
        private List<CustomWay> ways;
        private Map<Long, CustomNode> nodes;

    @Override
    public void parse(String path) throws FileNotFoundException {

    }

    public XMLVisualizationStump(){
            this.nodes = new HashMap<>();
            CustomNode a = new CustomNode(571329L, 441840, 6049500);
            CustomNode b = new CustomNode(571328L, 892460, 6401000);
            CustomNode c = new CustomNode(571327L, 441840, 6401000);
            CustomNode d = new CustomNode(545676L, 892460, 6049500);
            nodes.put(571329L, a); nodes.put(571328L, b); nodes.put(571327L, c); nodes.put(545676L, d);
            this.ways = new ArrayList<>();
            CustomWay abcd = new CustomWay(2080L, new ArrayList<Long>(Arrays.asList(571329L, 571328L, 571327L, 545676L)), "");
            this.ways.add(abcd);
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

