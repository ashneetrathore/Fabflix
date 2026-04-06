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
import java.sql.Statement;

@WebServlet(name = "AddStarServlet", urlPatterns = "/dashboard/addstar")
public class AddStarServlet extends HttpServlet {
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
            Statement statement1 = conn.createStatement();

            String query1 = "SELECT MAX(id) AS max_id" +
                            " FROM stars";

            ResultSet rs1 = statement1.executeQuery(query1);

            JsonObject starObject = new JsonObject();

            if (rs1.next()) {
                String name = request.getParameter("name");
                String birthYear = request.getParameter("birthYear");

                if (name != null && !name.isEmpty()) {
                    String query2 = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
                    PreparedStatement statement2 = conn.prepareStatement(query2);

                    String max_id = rs1.getString("max_id");
                    String part1 = max_id.replaceAll("[^A-Za-z]", "");
                    String part2 = max_id.replaceAll("[^0-9]", "");
                    Integer max_id_num = Integer.parseInt(part2);
                    Integer new_id_num = max_id_num + 1;
                    String new_id = part1 + new_id_num;

                    statement2.setString(1, new_id);
                    statement2.setString(2, name);

                    if (birthYear != null && !birthYear.isEmpty()) {
                        statement2.setInt(3, Integer.parseInt(birthYear));
                    }
                    else {
                        statement2.setNull(3, java.sql.Types.INTEGER);
                    }

                    statement2.executeUpdate();

                    String confirmationMsg = "Star " + new_id + " successfully added!";

                    starObject.addProperty("status", "success");
                    starObject.addProperty("message", confirmationMsg);
                }
                else {
                    String failureMsg = "ERROR: Star name is required";
                    starObject.addProperty("status", "fail");
                    starObject.addProperty("message", failureMsg);
                }

            }
            rs1.close();
            statement1.close();

            request.getServletContext().log("getting " + starObject.size() + " results");

            out.write(starObject.toString());
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