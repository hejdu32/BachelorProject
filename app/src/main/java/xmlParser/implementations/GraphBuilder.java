package xmlParser.implementations;

import xmlParser.framework.XMLParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GraphBuilder {

    private XMLParser xmlParser;

    public GraphBuilder(XMLParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public HashMap<Long, List<Edge>> createAdjencencyList(){
        HashMap<Long, List<Edge>> adjencencyList = new HashMap<>();
        for(CustomWay way: xmlParser.getWays()){
            long previousId = 0L;
            double previousDistance = 0.0;
            Iterator iterator = way.getNodeIdList().iterator();
            while(iterator.hasNext()) {
                if(previousId != 0L) {
                    previousId = way.getId();
                    iterator.next();
                }
                else {

                }
            }
        }


        return adjencencyList;
    }



}
