import java.util.List;

public class Star {
    private String id;
    private String name;
    private Integer birthYear;
    private List<Movie> movies;

    public Star() {}

    public Star(String id, String name, Integer birthYear, List<Movie> movies) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.movies = movies;
    }

    // Getters
    public String getId()          { return id; }
    public String getName()        { return name; }
    public Integer getBirthYear()  { return birthYear; }
    public List<Movie> getMovies() { return movies; }

    // Setters
    public void setId(String id)               { this.id = id; }
    public void setName(String name)           { this.name = name; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public void setMovies(List<Movie> movies)  { this.movies = movies; }
}