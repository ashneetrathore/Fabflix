package XMLParsing;

import java.util.ArrayList;

public class Movie {
    private String id;
    private String title;
    private Integer year;
    private String errorYear;
    private String director;

    private ArrayList<String> ListOfGenres;
    private ArrayList<String> wrongGenres;

    public Movie(String id, String title, Integer year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.ListOfGenres = new ArrayList<>();
        this.wrongGenres = new ArrayList<>();
    }

    public Movie() {
        this.ListOfGenres = new ArrayList<>();
        this.wrongGenres = new ArrayList<>();
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Integer getYear() { return this.year; }

    public String getErrorYear() { return this.errorYear; }

    public String getDirector() { return this.director; }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setYear(Integer year) { this.year = year; }

    public void setErrorYear(String errorYear) { this.errorYear = errorYear; }

    public void setDirector(String director) { this.director = director; }

    public void addGenre(String genre) { this.ListOfGenres.add(genre); }

    public void addWrongGenre(String wrongGenre) {
        this.wrongGenres.add(wrongGenre);
    }

    public ArrayList<String> getGenreList() { return this.ListOfGenres; }


    public String checkFields() {
        String msg = "";
        if (this.title == null) {
            msg += "No title name, Element name: t, Node Value: ''\n";
        }
        if (this.year == null) {
            if (this.errorYear == null) {
                msg += "No year name, Element name: year, Node Value: ''\n";
            }
            else {
                msg += "Invalid year type, Element name: t, Node Value: "
                        + this.errorYear + " \n";
            }
        }
        if (!this.wrongGenres.isEmpty()) {
            for (String wrongGenre : this.wrongGenres) {
                msg += "Invalid genre category, Element name: cat, Node Value: "
                        + "'" + wrongGenre + "'" + " \n";
            }
        }
        if (this.ListOfGenres.isEmpty()) {
            msg += "No genre names, Element name: cat, Node Value: ''\n";
        }
        return msg;
    }
}