import XMLParsing.Movie;
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
import java.text.DecimalFormat;
import java.util.Map;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/cart")
public class ShoppingCartServlet extends HttpServlet {
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

        PrintWriter out = response.getWriter();

        User customer = (User) request.getSession().getAttribute("user");
        String cartAction = request.getParameter("do");

        if (cartAction != null && cartAction.equals("add")) {
            try (Connection conn = dataSource.getConnection()) {
                String movie_id = request.getParameter("movie_id");

                String query1 = "SELECT m.id, m.title" +
                                " FROM movies AS m" +
                                " WHERE id = ?";

                PreparedStatement statement1 = conn.prepareStatement(query1);
                statement1.setString(1, movie_id);

                ResultSet rs1 = statement1.executeQuery();

                if (rs1.next()) {
                    String id = rs1.getString("id");
                    String title = rs1.getString("title");

                    JsonObject responseJsonObject = new JsonObject();

                    Map<Movie, Integer> currentCart = customer.getCart();

                    boolean added = false;
                    for (Movie m : currentCart.keySet()) {
                        String sameMovie = m.getId();
                        if (sameMovie.equals(movie_id)) {
                            customer.addToCart(m);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        Movie newMovie = new Movie(id, title, null, null);
                        customer.addToCart(newMovie);
                    }

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Successfully added to cart!");

                    out.write(responseJsonObject.toString());
                    response.setStatus(200);
                }
                rs1.close();
                statement1.close();

            } catch (Exception e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());

                request.getServletContext().log("Error:", e);
                response.setStatus(500);
            } finally {
                out.close();
            }
        }
        else {
            if (cartAction != null) {
                String movie_id = request.getParameter("movie_id");
                Map<Movie, Integer> currentCart = customer.getCart();
                switch(cartAction) {
                    case "increase":
                        for (Movie m : currentCart.keySet()) {
                            String id = m.getId();
                            if (id.equals(movie_id)) {
                                customer.addToCart(m);
                            }
                        }
                        break;
                    case "decrease":
                        for (Movie m : currentCart.keySet()) {
                            String id = m.getId();
                            if (id.equals(movie_id)) {
                                customer.decreaseCount(m);
                            }
                        }
                        break;
                    case "remove":
                        for (Movie m : currentCart.keySet()) {
                            String id = m.getId();
                            if (id.equals(movie_id)) {
                                customer.removeFromCart(m);
                                break;
                            }
                        }
                        break;
                }

            }
            Map<Movie, Integer> currentCart = customer.getCart();

            JsonArray jsonArray = new JsonArray();
            Double totalPrice = 0.0;
            for (Movie m : currentCart.keySet()) {
                JsonObject jsonObject = new JsonObject();

                String id = m.getId();
                String title = m.getTitle();
                String quantity = currentCart.get(m).toString();

                totalPrice += Double.parseDouble(quantity) * 9.99;

                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("quantity", quantity);
                jsonObject.addProperty("price", "$9.99");

                jsonArray.add(jsonObject);
            }

            DecimalFormat df = new DecimalFormat("#.00");
            String formattedTotalPrice = df.format(totalPrice);
            Double updatedTotalPrice = Double.parseDouble(formattedTotalPrice);

            JsonObject priceObject = new JsonObject();
            priceObject.addProperty("totalPrice", updatedTotalPrice);
            jsonArray.add(priceObject);

            customer.setTotalPrice(updatedTotalPrice);

            out.write(jsonArray.toString());
            response.setStatus(200);
        }
    }
}