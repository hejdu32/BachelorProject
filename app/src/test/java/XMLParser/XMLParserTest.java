/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package XMLParser;

import org.junit.Before;
import org.junit.Test;

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

}
