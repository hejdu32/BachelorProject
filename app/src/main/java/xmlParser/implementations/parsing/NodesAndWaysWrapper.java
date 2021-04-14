package xmlParser.implementations.parsing;

import java.util.Map;

public class NodesAndWaysWrapper {
    private int magicNumber;
    private Map<Long, CustomWay> ways;
    private Map<Long, CustomNode> nodes;

    public NodesAndWaysWrapper(int magicNumber, Map<Long, CustomWay> ways, Map<Long, CustomNode> nodes) {
        this.magicNumber = magicNumber;
        this.ways = ways;
        this.nodes = nodes;
    }
}
