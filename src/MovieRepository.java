import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovieRepository {
    private final DataSource dataSource;

    public MovieRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Movie findSingleMovie(String id) throws SQLException {
        String sql  = "SELECT m.id, m.title, m.year, m.director, r.rating"
                + " FROM movies AS m"
                + " LEFT JOIN ratings AS r ON r.movieId = m.id"
                + " WHERE m.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String movieId = rs.getString("id");
                return new Movie(
                        movieId,
                        rs.getString("title"),
                        rs.getInt("year"),
                        rs.getString("director"),
                        rs.getFloat("rating"),
                        findGenresByMovieId(movieId),
                        findStarsByMovieId(movieId)
                );
            }
        }
        return null;
    }

    public List<Movie> findMovies(MovieSearchParams params) throws SQLException {
        // Build the full SQL query
        QueryParts parts = buildQueryParts(params);

        String sql  = "SELECT m.id, m.title, m.year, m.director, r.rating"
                + " FROM movies AS m"
                + " LEFT JOIN ratings AS r ON r.movieId = m.id"
                + " WHERE " + parts.condition
                + parts.orderBy
                + " LIMIT " + params.getPerPage()
                + " OFFSET " + ((params.getPageNum() - 1) * params.getPerPage());

        List<Movie> movies = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < parts.placeholders.size(); i++) {
                stmt.setObject(i + 1, parts.placeholders.get(i));
            }

            // Execute the query
            ResultSet rs = stmt.executeQuery();
            // Loop through the result, building an array of the resulting movies
            while (rs.next()) {
                String movieId = rs.getString("id");
                Movie movie = new Movie(
                        movieId,
                        rs.getString("title"),
                        rs.getInt("year"),
                        rs.getString("director"),
                        rs.getFloat("rating"),
                        findGenresByMovieId(movieId),
                        findStarsByMovieId(movieId)
                );

                movies.add(movie);
            }
        }

        // Return the array for display to the frontend
        return movies;
    }

    public List<Genre> findGenresByMovieId(String movieId) throws SQLException {
        String sql = "SELECT g.name"
                + " FROM genres AS g"
                + " JOIN genres_in_movies AS gim ON g.id = gim.genreId"
                + " WHERE gim.movieId = ?"
                + " ORDER BY g.name"
                + " LIMIT 3";

        List<Genre> genres = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movieId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setName(rs.getString("name"));
                genres.add(genre);
            }
        }
        return genres;
    }

    public List<Star> findStarsByMovieId(String movieId) throws SQLException {
        String sql = "SELECT s.id, s.name"
                + " FROM stars s"
                + " JOIN stars_in_movies AS sim ON s.id = sim.starId"
                + " WHERE sim.movieId = ?"
                + " ORDER BY (SELECT COUNT(*) FROM stars_in_movies WHERE starId = s.id) DESC, s.name ASC"
                + " LIMIT 3";

        List<Star> stars = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, movieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Star star = new Star();
                star.setId(rs.getString("id"));
                star.setName(rs.getString("name"));
                stars.add(star);
            }
        }
        return stars;
    }

    public int countMovies(MovieSearchParams params) throws SQLException {
        QueryParts parts = buildQueryParts(params);

        String sql = "SELECT COUNT(*) AS movieCount"
                + " FROM movies AS m"
                + " LEFT JOIN ratings AS r ON r.movieId = m.id"
                + " WHERE " + parts.condition;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < parts.placeholders.size(); i++) {
                stmt.setObject(i + 1, parts.placeholders.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("movieCount");
            }
        }
        return 0;
    }

    private static class QueryParts {
        String condition;
        String orderBy;
        List<Object> placeholders;

        QueryParts(String condition, String orderBy, List<Object> placeholders) {
            this.condition = condition;
            this.orderBy = orderBy;
            this.placeholders = placeholders;
        }
    }

    private QueryParts buildQueryParts(MovieSearchParams params) {
        List<Object> placeholders = new ArrayList<>();
        String condition = buildConditions(params, placeholders);
        String orderBy = buildOrderBy(params.getOrderBy());
        return new QueryParts(condition, orderBy, placeholders);
    }

    private String buildConditions(MovieSearchParams params, List<Object> placeholders) {
        // Search by genre
        if (params.getGenre() != null && !params.getGenre().isEmpty()) {
            placeholders.add(params.getGenre());
            return "m.id in (SELECT gim.movieId"
                    + " FROM genres_in_movies AS gim"
                    + " JOIN (SELECT id FROM genres WHERE name = ?) AS g ON g.id = gim.genreId)";
        }
        // Search by first character of title
        if (params.getFirstChar() != null && !params.getFirstChar().isEmpty()) {
            if ("*".equals(params.getFirstChar())) {
                return "title REGEXP '^[^A-Za-z0-9]'";
            }
            else {
                placeholders.add(params.getFirstChar() + "%");
                return "title LIKE ?";
            }
        }
        // Search by any combination of title, year, director, star name
        List<String> conditions = new ArrayList<>();

        if (params.getTitle() != null && !params.getTitle().isEmpty()) {
            String fullText = "";
            for (String keyword :params.getTitle().split(" ")) {
                keyword = keyword.trim();
                if (!keyword.isEmpty()) {
                    fullText += "+" + keyword + "* ";
                }
            }
            conditions.add("MATCH (title) AGAINST (? IN BOOLEAN MODE)");
            placeholders.add(fullText.trim());
        }

        if (params.getYear() != null && !params.getYear().isEmpty()) {
            conditions.add("year = ?");
            placeholders.add(Integer.parseInt(params.getYear()));
        }

        if (params.getDirector() != null && !params.getDirector().isEmpty()) {
            conditions.add("director LIKE ?");
            placeholders.add("%" + params.getDirector() + "%");
        }

        if (params.getStarName() != null && !params.getStarName().isEmpty()) {
            conditions.add("m.id IN (SELECT sim.movieId"
                    + " FROM stars_in_movies AS sim"
                    + " JOIN (SELECT id FROM stars WHERE name LIKE ?) AS s ON s.id = sim.starId)");
            placeholders.add("%" + params.getStarName() + "%");
        }

        // If there are no conditions, return "1=1" to bypass the WHERE class
        // Otherwise, chain the conditions together with an "AND"
        return conditions.isEmpty() ? "1=1" : String.join(" AND ", conditions);
    }

    private String buildOrderBy(String orderBy) {
        if (orderBy == null || orderBy.isEmpty()) {
            // Default ordering
            return " ORDER BY m.title ASC, r.rating ASC";
        }
        switch (orderBy) {
            case "tArA": return " ORDER BY m.title ASC, r.rating ASC";
            case "tDrD": return " ORDER BY m.title DESC, r.rating DESC";
            case "tArD": return " ORDER BY m.title ASC, r.rating DESC";
            case "tDrA": return " ORDER BY m.title DESC, r.rating ASC";
            case "rAtA": return " ORDER BY r.rating ASC, m.title ASC";
            case "rDtD": return " ORDER BY r.rating DESC, m.title DESC";
            case "rAtD": return " ORDER BY r.rating ASC, m.title DESC";
            case "rDtA": return " ORDER BY r.rating DESC, m.title ASC";
            default:     return " ORDER BY m.title ASC, r.rating ASC";
        }
    }
}
