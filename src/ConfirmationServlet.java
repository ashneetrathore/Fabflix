import XMLParsing.Movie;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirm")
public class ConfirmationServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            User customer = (User) request.getSession().getAttribute("user");

            Integer customerId = customer.getId();
            Map<Movie, Integer> finalCart = customer.getCart();

            String query1 = "INSERT INTO sales(customerId, movieId, salesDate, copies) VALUES (?, ?, ?, ?)";
            String query2 = "SELECT s.id" +
                    " FROM sales AS s" +
                    " WHERE s.customerId = ?" +
                    " AND s.movieId = ?";

            PreparedStatement statement1 = conn.prepareStatement(query1);
            PreparedStatement statement2 = conn.prepareStatement(query2);

            JsonArray confirmationArray = new JsonArray();

            for (Movie m : finalCart.keySet()) {
                JsonObject itemObject = new JsonObject();

                String movie_id = m.getId();
                Integer quantity = finalCart.get(m);

                if (quantity != 0) {

                    statement1.setInt(1, customerId);
                    statement1.setString(2, movie_id);
                    statement1.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    statement1.setInt(4, quantity);
                    statement1.executeUpdate();

                    statement2.setInt(1, customerId);
                    statement2.setString(2, movie_id);

                    ResultSet rs2 = statement2.executeQuery();

                    if (rs2.next()) {
                        String sales_id = rs2.getString("id");
                        String movie_title = m.getTitle();

                        itemObject.addProperty("salesId", sales_id);
                        itemObject.addProperty("movieTitle", movie_title);
                        itemObject.addProperty("quantity", quantity.toString());
                    }
                    confirmationArray.add(itemObject);
                    rs2.close();
                }

            }
            statement1.close();
            statement2.close();
            Double totalPrice = customer.getTotalPrice();

            JsonObject priceObject = new JsonObject();
            priceObject.addProperty("totalPrice", totalPrice);

            confirmationArray.add(priceObject);

            out.write(confirmationArray.toString());

            customer.getCart().clear();
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