package xmlParser.framework;

import xmlParser.implementations.parsing.CustomNode;
import xmlParser.implementations.parsing.CustomWay;
import xmlParser.implementations.util.DistanceCalculatorImpl;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface XMLParser {

    void parse(String path) throws FileNotFoundException;

    Map<Long, CustomWay> getWays();

    Set<Long> getNodesToSearchFor();

    Map<Long, CustomNode> getNodes();

    DistanceCalculatorImpl getDistanceCalculator();

}
