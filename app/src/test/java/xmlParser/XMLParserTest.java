/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package xmlParser;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import xmlParser.implementations.MyWay;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class XMLParserTest {
    private XMLParser parser;

    @Before
    public void setUp() {parser = new XMLParser();}

    @Test
    public void testAppHasAGreeting() {
        assertNotNull("app should have a greeting", parser.getGreeting());
    }

    @Test
    public void testReader(){
        System.out.println("Please");
        try {
            parser.runReader("s");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkForExistanceOfTwoKnownWays(){
        try {
            parser.runReader("s");
            long id1 = 0;
            long id2 = 0;
            for(MyWay myWay: parser.getWays()) {
                long id = myWay.getId();
                if(id == 279060626){
                    id1 = id;
                }
                if(id == 616476468){
                    id2 = id;
                }
            }
            assertEquals(279060626, id1);
            assertEquals(616476468, id2);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
