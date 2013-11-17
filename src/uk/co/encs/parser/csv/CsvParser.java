/**
 * Enterprise Computer Solutions http://www.encs.co.uk
 */
package uk.co.encs.parser.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import uk.co.encs.parser.util.StringParser;
import uk.co.encs.transformation.engine.parser.DocumentParser;

/**
 * @author Chris Keeley ECS http://www.encs.co.uk 
 * This is the CSV parser module implementation of DocumentParser. 
 * Parse in a CSV file and transform the data into a generic XML schema.
 */
public class CsvParser implements DocumentParser {

    private final String ROOT_NODE_ELEMENT = "csvdocument";
    private final String LINE_NODE_ELEMENT = "row";
    private final String DATA_NODE_ELEMENT = "data";
    private Logger logger = Logger.getLogger(this.getClass().toString());
    static final Attributes __EMPTY_ATTR = new AttributesImpl();
    private String previousHeader;

    /**
     * @see uk.co.encs.transformation.engine.parser.DocumentParser#parseDocument(org.xml.sax.InputSource, org.xml.sax.ContentHandler)
     * @param source
     * @param ch
     * @throws IOException
     * @throws SAXException
     */
    public void parseDocument(final InputSource source, ContentHandler ch) throws IOException, SAXException {
        StringParser stringParser = new StringParser();
        String currentLine = null;
        String currentHeader = null;
        BufferedReader buffer = null;

        if (null == ch) {
            this.logger.fatal("Bad content handler!");
            throw new SAXException("Bad content handler");
        } else {
            buffer = this.inputSourceToBufferedReader(source);
            ch.startDocument();
            ch.startElement("", "", this.ROOT_NODE_ELEMENT, __EMPTY_ATTR); // root node
            while (null != (currentLine = buffer.readLine())) {
                currentLine = currentLine.trim();
                this.logger.debug(currentLine);
                int len = currentLine.length();
                if (len > 0) {
                    if (stringParser.isHeader(currentLine)) {
                        currentHeader = stringParser.topAndTail(currentLine);
                        if (this.previousHeader != null) {
                            ch.endElement("", "", this.previousHeader);
                        }
                        this.previousHeader = currentHeader;
                        // TODO work around the EOR hack.
                        // quick hack to make sure its not the EOR header 
                        // its a pointless tag for the xml as its a file delimeter and *never* contains data
                        if (currentHeader.compareTo("EOR") != 0) {
                            ch.startElement("", "", currentHeader, __EMPTY_ATTR);
                        }
                    } else {
                        // parse a line of data values
                        ch.startElement("", "", this.LINE_NODE_ELEMENT, __EMPTY_ATTR);
                        ch = this.buildNodes(ch, stringParser.tokenizeString(currentLine));
                        ch.endElement("", "", this.LINE_NODE_ELEMENT);
                    }
                }
                /*
                 * empty lines are ignored
                 *
                 * else if(len == 0) { ch.characters(this.EMPTY_NODE_ELEMENT.toCharArray(), 0, this.EMPTY_NODE_ELEMENT.length()); }
                 */
            }
            if (currentLine == null) {
                ch.endElement("", "", currentHeader);
            }
            /**
             * @todo We may have to terminate an open header here
             */
            ch.endElement("", "", this.ROOT_NODE_ELEMENT);
            ch.endDocument();
        }
    }

    /**
     * Construct nodes based on consecutive tokens extracted from a line of data e.g: <csvData> <csvData0>A Token</csvData0> <csvData1>Another Token</csvData1> </csvData>
     *
     * @param ch
     * @param tokens
     * @return the populated content handler
     * @throws SAXException
     */
    private ContentHandler buildNodes(ContentHandler ch, ArrayList<String> tokens) throws SAXException {
        Iterator<String> iter = tokens.iterator();
        int counter = 0;
        while (iter.hasNext()) {
            ch.startElement("", "", this.DATA_NODE_ELEMENT + counter, __EMPTY_ATTR);
            String data = iter.next();
            ch.characters(data.toCharArray(), 0, data.length());
            ch.endElement("", "", this.DATA_NODE_ELEMENT + counter);
            counter++;
        }
        return ch;
    }

    /**
     * @see uk.co.encs.transformation.engine.parser.DocumentParser#inputSourceToBufferedReader(InputSource) inputSourceToBufferedReader
     * @param source
     * @return the buffered reader obtained from the input source
     * @throws SAXException
     */
    private BufferedReader inputSourceToBufferedReader(InputSource source) throws SAXException {
        BufferedReader buffer = null;
        if (source.getCharacterStream() != null) {
            buffer = new BufferedReader(source.getCharacterStream());
        } else if (source.getByteStream() != null) {
            buffer = new BufferedReader(new InputStreamReader(source.getByteStream()));
        } else if (source.getSystemId() != null) {
            try {
                java.net.URL url = new URL(source.getSystemId());
                buffer = new BufferedReader(new InputStreamReader(url.openStream()));
            } catch (MalformedURLException urlexc) {
                this.logger.fatal("Bad URL" + urlexc.getMessage());
            } catch (IOException ioexc) {
                this.logger.fatal("IO Error" + ioexc.getMessage());
            }
        } else {
            this.logger.warn("Invalid inputsource");
            throw new SAXException("Invalid InputSource object");
        }
        return buffer;
    }
}