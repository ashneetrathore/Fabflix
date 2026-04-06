import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreRepository {
    private final DataSource dataSource;

    public GenreRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Genre> findAllGenres() throws SQLException {
        String sql = "SELECT name"
                + " FROM genres"
                + " ORDER BY name";

        List<Genre> genres = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Genre genre = new Genre();
                genre.setName(rs.getString("name"));
                genres.add(genre);
            }
        }
        return genres;
    }
}
