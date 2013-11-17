/**
 * Enterprise Computer Solutions 
 * http://www.encs.co.uk 
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.Logger;

import uk.co.encs.parser.util.StringParser;


/**
 * @author Chris Keeley ECS http://www.encs.co.uk
 * This class tests the string parsing abilities of the StringParser object
 */
public class TestStringParser extends TestCase {
	Logger logger = Logger.getLogger(this.getClass());
	/**
	 * Test StringParser functionality
	 * @param test
	 */
	public TestStringParser(final String test) {
		super(test);
	}
	/**
	 * Execute test suites
	 * @return suite the current set of test suites
	 */
	public static Test suite( ) {
		TestSuite suite = new TestSuite();
		suite.addTest(new TestStringParser("testParseLine"));
		return suite;
	}
	/**
	 * 
	 *
	 */
	public void testParseLine() {
		StringParser parser = new StringParser();
		String filePath = "input\\test.csv";
		ArrayList<String> tokens = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while((line = reader.readLine() ) != null) {
				tokens = parser.tokenizeString(line);
				Iterator<String> iter = tokens.iterator();
				while(iter.hasNext()) {
					String token = iter.next();
					if(parser.isHeader(token)) {
						this.logger.debug("Header encountered: "+token+" ");
					} else {
						this.logger.debug("Token: "+token);
					}
				}
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
 	}
	
}
