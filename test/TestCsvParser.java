/**
 * Enterprise Computer Solutions http://www.encs.co.uk
 */

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.co.encs.parser.csv.CsvParser;

/**
 * @author Chris Keeley ECS http://www.encs.co.uk
 *
 */
public class TestCsvParser extends TestCase {

    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * @param test
     */
    public TestCsvParser(String test) {
        super(test);
    }

    /**
     *
     * @return the current test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new TestCsvParser("testParse"));
        return suite;
    }

    /**
     *
     *
     */
    public void testParse() {
        CsvParser parser = new CsvParser();
        InputSource csvSource = null;

        //String filePathName = "input/test.csv";
        String filePathName = "input/random.csv";

        Logger logger = Logger.getLogger(this.getClass().toString());
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        TransformerHandler transformerHandler = null;
        if (transformerFactory.getFeature(SAXTransformerFactory.FEATURE)) {
            final SAXTransformerFactory saxTransformerFactory = (SAXTransformerFactory) transformerFactory;
            try {
                transformerHandler = saxTransformerFactory.newTransformerHandler();
            } catch (TransformerConfigurationException ex1) {
                ex1.printStackTrace();
            }
            if (transformerHandler != null) {
                StreamResult outputStream = new StreamResult(new ByteArrayOutputStream());
                transformerHandler.setResult(outputStream);
                try {
                    csvSource = new InputSource(new FileReader(filePathName));
                    parser.parseDocument(csvSource, transformerHandler);
                    String result = outputStream.getOutputStream().toString();
                    this.logger.debug(result);
                    BufferedWriter out = new BufferedWriter(new FileWriter("output/test.xml"));
                    out.write(result);
                    out.close();
                } catch (FileNotFoundException ex) {
                    logger.fatal("FileNotFoundException - Could not find:" + filePathName);
                } catch (IOException ex) {
                    // TODO
                    ex.printStackTrace();
                } catch (SAXException ex) {
                    // TODO
                    ex.printStackTrace();
                }
            }
        }
    }
}
