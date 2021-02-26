package xmlParser.framework;

import xmlParser.implementations.CustomNode;
import xmlParser.implementations.CustomWay;
import xmlParser.implementations.DistanceCalculatorImpl;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface XMLParser {

    void parse(String path) throws FileNotFoundException;

    List<CustomWay> getWays();

    Set<Long> getNodesToSearchFor();

    Map<Long, CustomNode> getNodes();

    DistanceCalculatorImpl getDistanceCalculator();

}
