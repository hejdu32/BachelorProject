package utilTesting;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;
import xmlParser.implementations.util.FileWriter;

import java.io.FileNotFoundException;

public class utilTest {

    @Before
    public void setUp() {

    }

    @Test
    public void maltaFile(){
        FileWriter.parseCountryToFile("malta");
    }

    @Test
    public void denmarkFile(){
        FileWriter.parseCountryToFile("denmark");
    }
}
