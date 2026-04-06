import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StarRepository {
    private final DataSource dataSource;

    public StarRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Star findSingleStar(String id) throws SQLException {
        String sql  = "SELECT id, name, birthYear"
                + " FROM stars"
                + " WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String starId = rs.getString("id");

                Star star = new Star();
                star.setId(starId);
                star.setName(rs.getString("name"));
                star.setMovies(findMoviesByStarId(starId));

                int birthYear = rs.getInt("birthYear");
                star.setBirthYear(rs.wasNull() ? null : birthYear);

                return star;
            }
        }
        return null;
    }

    public List<Movie> findMoviesByStarId(String starId) throws SQLException {
        String sql = "SELECT m.id, m.title"
                + " FROM movies AS m"
                + " JOIN stars_in_movies AS sim ON m.id = sim.movieId"
                + " WHERE sim.starId = ?"
                + " ORDER BY m.year DESC, m.title ASC";

        List<Movie> movies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, starId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getString("id"));
                movie.setTitle(rs.getString("title"));
                movies.add(movie);
            }
        }
        return movies;
    }
}
