package XMLParsing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
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

public class SAXMovieParser extends DefaultHandler {
    private PrintWriter movieWriter;
    private PrintWriter genreWriter;
    private PrintWriter gimWriter;
    private PrintWriter errorWriter;
    private String tempDirector;
    private Movie tempMovie;
    private String tempVal;
    private int maxGenreId;

    private HashMap<String, String> genreCodes;
    private HashMap<String, Integer> addedGenreToFile;
    private HashSet<String> existingMovies;
    private HashSet<String> existingMovieIds;

    public SAXMovieParser() {
        this.genreCodes = new HashMap<>();
        this.addedGenreToFile = new HashMap<>();
        this.existingMovies = new HashSet<>();
        this.existingMovieIds = new HashSet<>();

        genreCodes.put("Actn", "Action");
        genreCodes.put("Advt", "Adventure");
        genreCodes.put("AvGa", "Avant Garde");
        genreCodes.put("BioP", "Biography");
        genreCodes.put("Camp", "Camp");
        genreCodes.put("Cart", "Cartoon");
        genreCodes.put("CnR", "Cops and Robbers");
        genreCodes.put("Comd", "Comedy");
        genreCodes.put("Disa", "Disaster");
        genreCodes.put("Docu", "Documentary");
        genreCodes.put("Dram", "Drama");
        genreCodes.put("Epic", "Epic");
        genreCodes.put("Faml", "Family");
        genreCodes.put("Hist", "History");
        genreCodes.put("Horr", "Horror");
        genreCodes.put("Musc", "Musical");
        genreCodes.put("Myst", "Mystery");
        genreCodes.put("Noir", "Black");
        genreCodes.put("Porn", "Pornography");
        genreCodes.put("Romt", "Romance");
        genreCodes.put("ScFi", "Sci-Fi");
        genreCodes.put("Surl", "Surreal");
        genreCodes.put("Susp", "Thriller");
        genreCodes.put("West", "Western");

        setMaxGenreId();
    }

    public HashSet<String> getExistingMovies() {
        return this.existingMovieIds;
    }

    private void setMaxGenreId() {
        try {
            String url = "jdbc:mysql://localhost:3306/moviedb";
            String user = "my_db_user";
            String password = "my_db_pwd";
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS max_id FROM genres");
            rs.next();
            int max_id = rs.getInt(1);
            rs.close();
            stmt.close();
            conn.close();
            this.maxGenreId = max_id;
        }
        catch (Exception e) {
            this.maxGenreId = 0;
        }
    }


    public void runParse() throws IOException {
        openFiles();
        parseDocument();
        closeFiles();
    }

    private void openFiles() throws FileNotFoundException, UnsupportedEncodingException {
        movieWriter = new PrintWriter("movies.txt", "UTF-8");
        genreWriter = new PrintWriter("genres.txt", "UTF-8");
        gimWriter = new PrintWriter("gim.txt", "UTF-8");
        errorWriter = new PrintWriter("movieInconsistency.md", "UTF-8");
    }
    private void closeFiles() {
        if (movieWriter != null) {
            movieWriter.close();
        }
        if (genreWriter != null) {
            genreWriter.close();
        }
        if (gimWriter != null) {
            gimWriter.close();
        }
        if (errorWriter != null) {
            errorWriter.close();
        }

    }
    private void populateFiles(){
        String msg = tempMovie.checkFields();
        if (msg == "" && !existingMovies.contains(tempMovie.getTitle())) {
            movieWriter.printf("%s\t%s\t%d\t%s\n", tempMovie.getId(),
                    tempMovie.getTitle(),
                    tempMovie.getYear(),
                    tempMovie.getDirector());

            existingMovies.add(tempMovie.getTitle());
            existingMovieIds.add(tempMovie.getId());

            ArrayList<String> genreList = tempMovie.getGenreList();
            ArrayList<Integer> genreExistsList = checkGenreExists(genreList);
            for (int i = 0; i < genreList.size(); i++) {
                if (genreExistsList.get(i) == 0) {
                    genreWriter.printf("%s\n", genreList.get(i));
                }
            }
            for (int i = 0; i < genreList.size(); i++) {
                gimWriter.printf("%d\t%s\n", addedGenreToFile.get(genreList.get(i)), tempMovie.getId());
            }
        }
        if (msg != "") {
            errorWriter.printf("%s\n", msg);
        }

    }

    private ArrayList<Integer> checkGenreExists(ArrayList<String> genreList) {
        try {
            String url = "jdbc:mysql://localhost:3306/moviedb";
            String user = "my_db_user";
            String password = "my_db_pwd";
            Connection conn = DriverManager.getConnection(url, user, password);

            String query1 = "SELECT COUNT(*) AS genreCount FROM genres WHERE name = ?";
            PreparedStatement statement1 = conn.prepareStatement(query1);

            String query2 = "SELECT id FROM genres WHERE name = ?";
            PreparedStatement statement2 = conn.prepareStatement(query2);
            ArrayList<Integer> genreExistsBoolean = new ArrayList<>();

            for (int i = 0; i < genreList.size(); i++) {
                if (!addedGenreToFile.containsKey(genreList.get(i))) {
                    statement1.setString(1, genreList.get(i));
                    ResultSet rs = statement1.executeQuery();
                    rs.next();
                    int genreExists = rs.getInt(1);
                    genreExistsBoolean.add(genreExists);
                    rs.close();

                    int genreId;

                    if (genreExists == 1) {
                        statement2.setString(1, genreList.get(i));
                        ResultSet rs2 = statement2.executeQuery();
                        rs2.next();
                        genreId = rs2.getInt(1);
                        rs2.close();
                    }
                    else {
                        maxGenreId++;
                        genreId = maxGenreId;
                    }
                    addedGenreToFile.put(genreList.get(i), genreId);
                }
                else {
                    genreExistsBoolean.add(1);
                }

            }
            statement1.close();
            conn.close();
            return genreExistsBoolean;
        }
        catch (Exception e) {
            return new ArrayList<Integer>();
        }
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            InputSource is = new InputSource("xmlfiles/mains243.xml");
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
        if (qName.equalsIgnoreCase("film")) { // new Movie
            tempMovie = new Movie();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("dirname")) { // get director
            tempDirector = tempVal;
        } else if (qName.equalsIgnoreCase("fid")) { // set movie id
            tempMovie.setId(tempVal);
            tempVal = "";
        } else if (qName.equalsIgnoreCase("t")) { // set title
            if (!tempVal.isEmpty()) {
                tempMovie.setTitle(tempVal);
            }
        } else if (qName.equalsIgnoreCase("year")) { // set year
            try {
                tempMovie.setYear(Integer.parseInt(tempVal));
            } catch (Exception e) {
                tempMovie.setYear(null);
                tempMovie.setErrorYear(tempVal);
            }
            tempVal = "";
        } else if (qName.equalsIgnoreCase("film")) {
            tempMovie.setDirector(tempDirector); // set director
            populateFiles();
        } else if (qName.equalsIgnoreCase("cat")) {
            if (tempVal != "") {
                if (genreCodes.containsKey(tempVal)) {
                    tempMovie.addGenre(genreCodes.get(tempVal));
                }
                else {
                    tempMovie.addWrongGenre(tempVal);
                }
            }
        }

    }
}