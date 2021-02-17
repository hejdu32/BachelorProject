package XMLParser;

import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.Source;

public class MyOsmWriter implements Source{
    private  Sink sink;

    @Override
    public void setSink(Sink sink) {
    this.sink = sink;
    }


}
