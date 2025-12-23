package XMLParsing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXCastParser extends DefaultHandler {
    private PrintWriter simWriter;;
    private PrintWriter errorWriter;
    private String tempMovieId;
    private String tempStar;
    private String tempVal;
    private int error;
    private String msg;

    private HashMap<String, String> existingStars;
    private HashSet<String> existingMovies;

    private ArrayList<String> rowsToWrite1;
    private ArrayList<String> rowsToWrite2;

    public SAXCastParser(SAXMovieParser spe1, SAXActorParser spe2) {
        existingMovies = spe1.getExistingMovies();
        existingStars = spe2.getExistingStars();

        msg = "";
        error = 0;
        this.rowsToWrite1 = new ArrayList<>();
        this.rowsToWrite2 = new ArrayList<>();
    }

    public void runParse() throws IOException {
        openFiles();
        parseDocument();
        populateRemainingRecords();
        closeFiles();
    }

    private void openFiles() throws FileNotFoundException, UnsupportedEncodingException {
        simWriter = new PrintWriter("sim.txt", "UTF-8");
        errorWriter = new PrintWriter ("starsInMovieInconsistency.md", "UTF-8");
    }
    private void closeFiles() {
        if (simWriter != null) {
            simWriter.close();
        }
        if (errorWriter != null) {
            errorWriter.close();
        }

    }
    private void populateFiles(){
        if (error == 0) {
            rowsToWrite1.add(String.format("%s\t%s\n", existingStars.get(tempStar), tempMovieId));

            if (rowsToWrite1.size() >= 1000) {
                for (String row : rowsToWrite1) {
                    simWriter.print(row);
                }
                rowsToWrite1.clear();
            }
        }
        else {
            rowsToWrite2.add(String.format("%s\n", msg));
            if (rowsToWrite2.size() >= 1000) {
                for (String row : rowsToWrite2) {
                    errorWriter.print(row);
                }
                rowsToWrite2.clear();
            }
        }
        error = 0;
    }

    private void populateRemainingRecords() {
        for (String row : rowsToWrite1) {
            simWriter.print(row);
        }
        rowsToWrite1.clear();
        for (String row: rowsToWrite2) {
            errorWriter.print(row);
        }
        rowsToWrite2.clear();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            InputSource is = new InputSource("xmlfiles/casts124.xml");
            is.setEncoding("ISO-8859-1");

            SAXParser sp = spf.newSAXParser();
            sp.parse(is, this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("a")) {
            tempVal = "";
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("f")) {
            error = 0;
            msg = "";
            tempMovieId = tempVal;

            if (!existingMovies.contains(tempMovieId)) {
                error = 1;
                msg += "Invalid movie id, Element name: f, Node Value: " + tempMovieId + " \n";
            }

        } else if (error == 0 && qName.equalsIgnoreCase("a")) {
            tempStar = tempVal;
            if (tempStar.isEmpty()) {
                error = 1;
            }
            else if (!existingStars.containsKey(tempStar)) {
                error = 1;
                msg += "Star name doesn't exist in database, Element name: a, Node Value: " + tempStar + " \n";
            }
        } else if (qName.equalsIgnoreCase("m")) {
            populateFiles();
        }

    }

    public static void main(String[] args) throws IOException {
        SAXMovieParser spe1 = new SAXMovieParser();
        spe1.runParse();

        SAXActorParser spe2 = new SAXActorParser();
        spe2.runParse();

        SAXCastParser spe3 = new SAXCastParser(spe1, spe2);
        spe3.runParse();
    }
}