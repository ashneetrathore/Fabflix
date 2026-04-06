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
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/dashboard/addmovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String movie_title = request.getParameter("title");
            String movie_year = request.getParameter("year");
            String movie_director = request.getParameter("director");
            String star_name = request.getParameter("sname");
            String birthYear = request.getParameter("birthYear");
            String genre_name = request.getParameter("genre");

            JsonObject msgObject = new JsonObject();

            if (movie_title != null && !movie_title.isEmpty() &&
                    movie_year != null && !movie_year.isEmpty() &&
                    movie_director != null && !movie_director.isEmpty() &&
                    star_name != null && !star_name.isEmpty() &&
                    genre_name != null && !genre_name.isEmpty()) {

                String callString = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?)}";
                CallableStatement statement1 = conn.prepareCall(callString);
                statement1.setString(1, movie_title);
                statement1.setInt(2, Integer.parseInt(movie_year));
                statement1.setString(3, movie_director);
                statement1.setString(4, star_name);
                if (birthYear != null && !birthYear.isEmpty()) {
                    statement1.setInt(5, Integer.parseInt(birthYear));
                } else {
                    statement1.setNull(5, java.sql.Types.INTEGER);
                }

                statement1.setString(6, genre_name);

                statement1.registerOutParameter(7, java.sql.Types.VARCHAR);
                statement1.execute();
                String msg = statement1.getString(7);

                msgObject.addProperty("status", "success");
                msgObject.addProperty("message", msg);
                statement1.close();
            }
            else {
                String failureMsg = "ERROR: Fill out all required fields";
                msgObject.addProperty("status", "fail");
                msgObject.addProperty("message", failureMsg);

            }
            request.getServletContext().log("getting " + msgObject.size() + " results");
            out.write(msgObject.toString());
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