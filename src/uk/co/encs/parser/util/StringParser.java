/**
 * Enterprise Computer Solutions 
 * http://www.encs.co.uk 
 */
package uk.co.encs.parser.util;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * @author Chris Keeley ECS http://www.encs.co.uk
 *
 */
public class StringParser {
	private int lineCount;
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * class constructor 
	 * Init member attributes
	 */
	public StringParser() {
		lineCount = 0;
	}
	
	/**
	 * This method removes the first and last characters from a string
	 * @param token 
	 * @return token the header with delimeters removed
	 */
	public String topAndTail(String token) {
		if(isHeader(token)) {
			token = token.substring(1, token.length()-1);
		}
		return token;
	}
	
	/**
	 * Determine if the current token is a header
	 * @param token 
	 * @return true if its a header, false if not
	 */
	public boolean isHeader(String token) {
		if(token.startsWith("[") && token.endsWith("]")) {
			return true;
		} 
		return false;
	}
	
	/**
	 * Parse the current line into tokens
	 * @param currentLine 
	 * @return tokens the parsed tokens
	 */
	public ArrayList<String> tokenizeString(String currentLine) {
		ArrayList<String> tokenList = new ArrayList<String>();
		if(currentLine.length() > 0) {
			this.incrementLineCount();
			StringTokenizer tokenizer = new StringTokenizer(currentLine, ",");
			while(tokenizer.hasMoreTokens()) {
				String token = this.stripDelimeter(tokenizer.nextToken(), "'");
				if(token.length() > 0) {
					tokenList.add(token);
				}
			}
		} else {
			this.logger.debug("Ignoring blank line");
		}
		return tokenList;
	}
	
	/**
	 * Remove all occurances of delimeter from token
	 * @param token
	 * @param delimeter
	 * @return the string stripped of all occurences of the delimeter
	 */
	public String stripDelimeter(String token, String delimeter) {
		return token.replaceAll(delimeter, new String()).trim();
	}
	
	/**
	 * Increment the line count by one
	 */
	private void incrementLineCount() {
		this.lineCount++;
	}
	
	/**
	 * Set line count back to zero
	 */
	public void resetLineCount() {
		this.lineCount = 0;
	}
	
	/**
	 * Get the current line count
	 * @return the current line count
	 */
	public int getLineCount() {
		return this.lineCount;
	}
}
