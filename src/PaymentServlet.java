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
import java.text.SimpleDateFormat;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/pay")
public class PaymentServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbRead");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        User customer = (User) request.getSession().getAttribute("user");


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("totalPrice", customer.getTotalPrice());
        out.write(jsonObject.toString());
        response.setStatus(200);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String ccNum = request.getParameter("ccNum");
        String expDate = request.getParameter("expDate");

        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date expirationDate = new Date(dateFormat.parse(expDate).getTime());

            String query1 = "SELECT *" +
                            " FROM creditcards" +
                            " WHERE firstName = ?" +
                            " AND lastName = ?" +
                            " AND id = ?" +
                            " AND expiration = ?" +
                            " LIMIT 1";
            PreparedStatement statement1 = conn.prepareStatement(query1);
            statement1.setString(1, firstName);
            statement1.setString(2, lastName);
            statement1.setString(3, ccNum);
            statement1.setDate(4, expirationDate);

            ResultSet rs1 = statement1.executeQuery();

            JsonObject responseJsonObject = new JsonObject();

            if (rs1.next()) {
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else {
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Payment failed");
                responseJsonObject.addProperty("message", "Invalid information. Re-enter payment information");
            }

            rs1.close();
            statement1.close();

            out.write(responseJsonObject.toString());
            response.setStatus(200);

        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Payment failed");
            responseJsonObject.addProperty("message", "Invalid information. Re-enter payment information");

            out.write(responseJsonObject.toString());
        } finally {
            out.close();
        }

    }
}