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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    private MovieRepository movieRepo;

    public void init(ServletConfig config) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbRead");
            movieRepo = new MovieRepository(dataSource);
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
        PrintWriter out = response.getWriter();

        try {
            String id = request.getParameter("id");
            Movie m = movieRepo.findSingleMovie(id);

            JsonArray jsonArray = new JsonArray();

            if (m != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", m.getId());
                jsonObject.addProperty("movie_name", m.getTitle());
                jsonObject.addProperty("movie_yr", m.getYear());
                jsonObject.addProperty("movie_director", m.getDirector());

                // Build comma separated genre names
                StringBuilder genreNames = new StringBuilder();
                for (int i = 0; i < m.getGenres().size(); i++) {
                    if (i > 0) genreNames.append(",");
                    genreNames.append(m.getGenres().get(i).getName());
                }
                jsonObject.addProperty("movie_genres", genreNames.toString());

                // Build comma separated star names and ids for frontend links
                StringBuilder starNames = new StringBuilder();
                StringBuilder starIds = new StringBuilder();
                for (int i = 0; i < m.getStars().size(); i++) {
                    if (i > 0) {
                        starNames.append(",");
                        starIds.append(",");
                    }
                    starNames.append(m.getStars().get(i).getName());
                    starIds.append(m.getStars().get(i).getId());
                }
                jsonObject.addProperty("movie_stars", starNames.toString());
                jsonObject.addProperty("movie_star_ids", starIds.toString());

                jsonObject.addProperty("movie_rating", m.getRating());

                jsonArray.add(jsonObject);
            }

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
