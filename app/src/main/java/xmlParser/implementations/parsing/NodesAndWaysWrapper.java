package xmlParser.implementations.parsing;

import java.util.Collection;

public class NodesAndWaysWrapper {
    private int magicNumber;
    private Collection<CustomWay> ways;
    private Collection<CustomNode> nodes;

    public NodesAndWaysWrapper(int magicNumber, Collection<CustomWay> ways, Collection<CustomNode> nodes) {
        this.magicNumber = magicNumber;
        this.ways = ways;
        this.nodes = nodes;
    }
}
