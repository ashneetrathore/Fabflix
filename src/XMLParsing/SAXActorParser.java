package XMLParsing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXActorParser extends DefaultHandler {
    private PrintWriter starWriter;
    private Star tempStar;
    private String tempVal;
    private String maxStarId;

    private HashMap<String, String> existingStars;

    public SAXActorParser() {
        this.existingStars = new HashMap<>();
        setMaxStarId();
    }

    public HashMap<String, String> getExistingStars() {
        return this.existingStars;
    }

    private void setMaxStarId() {
        try {
            String url = "jdbc:mysql://localhost:3306/moviedb";
            String user = "your_db_user";
            String password = "your_db_pwd";
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM stars");
            rs.next();
            String max_id = rs.getString(1);
            rs.close();
            stmt.close();
            this.maxStarId = max_id;

            conn.close();
        }
        catch (Exception e) {
            this.maxStarId = "";
        }
    }

    private String incrementStarId() {
        String part1 = this.maxStarId.replaceAll("[^A-Za-z]", "");
        String part2 = this.maxStarId.replaceAll("[^0-9]", "");
        Integer max_id_num = Integer.parseInt(part2);
        Integer new_id_num = max_id_num + 1;
        String new_id = part1 + new_id_num;
        this.maxStarId = new_id;
        return new_id;
    }


    public void runParse() throws IOException {
        openFiles();
        parseDocument();
        closeFiles();
    }

    private void openFiles() throws FileNotFoundException, UnsupportedEncodingException {
        starWriter = new PrintWriter("stars.txt", "UTF-8");
    }
    private void closeFiles() {
        if (starWriter != null) {
            starWriter.close();
        }

    }
    private void populateFiles(){
        if (!existingStars.containsKey(tempStar.getName())) {
            String new_id = incrementStarId();
            if (tempStar.getDOB() != null) {
                starWriter.printf("%s\t%s\t%d\n", new_id,
                                                tempStar.getName(),
                                                tempStar.getDOB());
            }
            else {
                starWriter.printf("%s\t%s\n", new_id,
                                            tempStar.getName());
            }
            existingStars.put(tempStar.getName(), new_id);
        }

    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            InputSource is = new InputSource("xmlfiles/actors63.xml");
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
        if (qName.equalsIgnoreCase("stagename")) { // new Movie
            tempStar = new Star();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("stagename")) {
            tempStar.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempStar.setDOB(Integer.parseInt(tempVal));
            } catch (Exception e) {
                tempStar.setDOB(null);
            }
        }
        else if (qName.equalsIgnoreCase("actor")) {
            populateFiles();
        }

    }
}