import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbRead");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        String id = request.getParameter("id");

        request.getServletContext().log("getting id: " + id);

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {

            String query1 = "SELECT m.id, m.title, m.year, m.director, r.rating" +
                            " FROM movies AS m" +
                            " LEFT JOIN ratings AS r ON r.movieId = m.id" +
                            " WHERE m.id = ?" +
                            " ORDER BY rating DESC";

            PreparedStatement statement = conn.prepareStatement(query1);

            statement.setString(1, id);

            ResultSet rs1 = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            while (rs1.next()) {
                String movie_id = rs1.getString("id");
                String movie_name = rs1.getString("title");
                String movie_yr = rs1.getString("year");
                String movie_director = rs1.getString("director");
                String movie_rating = rs1.getString("rating");

                String query2 = "SELECT s.id, s.name" +
                        " FROM stars s" +
                        " WHERE s.id IN (SELECT starId" +
                        " FROM stars_in_movies" +
                        " WHERE movieId = ?)" +
                        " AND s.id IN (SELECT starId" +
                        " FROM stars_in_movies)" +
                        " ORDER BY (SELECT COUNT(starId)" +
                        " FROM stars_in_movies" +
                        " WHERE starId = s.id" +
                        " GROUP BY starId) DESC, s.name ASC";

                PreparedStatement statement2 = conn.prepareStatement(query2);

                statement2.setString(1, movie_id);

                ResultSet rs2 = statement2.executeQuery();

                StringBuilder allStars = new StringBuilder();
                StringBuilder allStarIds = new StringBuilder();
                int i = 0;
                while (rs2.next()) {
                    if (i > 0) allStars.append(",");
                    if (i > 0) allStarIds.append(",");
                    allStars.append(rs2.getString("name"));
                    allStarIds.append(rs2.getString("id"));
                    i++;
                }

                String query3 = "SELECT g.name" +
                                " FROM genres AS g" +
                                " JOIN (SELECT genreId" +
                                " FROM genres_in_movies" +
                                " WHERE movieId = ?) AS gim ON g.id = gim.genreId" +
                                " ORDER BY g.name";

                PreparedStatement statement3 = conn.prepareStatement(query3);

                statement3.setString(1, movie_id);

                ResultSet rs3 = statement3.executeQuery();

                StringBuilder allGenres = new StringBuilder();
                i = 0;
                while (rs3.next()) {
                    if (i > 0) allGenres.append(",");
                    allGenres.append(rs3.getString("name"));
                    i++;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_name", movie_name);
                jsonObject.addProperty("movie_yr", movie_yr);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", allGenres.toString());
                jsonObject.addProperty("movie_stars", allStars.toString());
                jsonObject.addProperty("movie_star_ids", allStarIds.toString());
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);

                rs2.close();
                rs3.close();
                statement2.close();
                statement3.close();
            }
            rs1.close();
            statement.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");

            out.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
