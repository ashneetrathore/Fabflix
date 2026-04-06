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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    private StarRepository starRepo;

    public void init(ServletConfig config) {
        try {
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbRead");
            starRepo = new StarRepository(dataSource);
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
            Star s = starRepo.findSingleStar(id);

            JsonArray jsonArray = new JsonArray();

            if (s != null) {
                for (Movie m : s.getMovies()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_name", s.getName());
                    jsonObject.addProperty("star_dob", s.getBirthYear());
                    jsonObject.addProperty("movie_id", m.getId());
                    jsonObject.addProperty("movie_name", m.getTitle());
                    jsonArray.add(jsonObject);
                }

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