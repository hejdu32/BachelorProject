package utilTesting;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.util.FileWriter;

import java.io.FileNotFoundException;

public class utilTest {
    FileWriter fw;

    @Before
    public void setUp() {
        fw = new FileWriter();
    }

    @Test
    public void maltaFile(){
        fw.parseCountryToFile("malta");
    }

    @Test
    public void denmarkFile(){
        fw.parseCountryToFile("denmark");
    }
}
