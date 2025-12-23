import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbRead");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        PrintWriter out = response.getWriter();


        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");

            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "ERROR: Recaptcha failed");
            out.write(responseJsonObject.toString());

            out.close();
            return;
        }

        try (Connection conn = dataSource.getConnection()) {
            String query1 = "SELECT *" +
                    " FROM customers" +
                    " WHERE email = ?" +
                    " LIMIT 1";

            PreparedStatement statement1 = conn.prepareStatement(query1);
            statement1.setString(1, email);

            ResultSet rs1 = statement1.executeQuery();

            JsonObject responseJsonObject = new JsonObject();
            boolean success;

            if (rs1.next()) {
                String encryptedPassword = rs1.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);

                if (success) {
                    Integer customerId = Integer.parseInt(rs1.getString("id"));
                    request.getSession().setAttribute("user", new User(customerId, email, password));


                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else {
                    responseJsonObject.addProperty("status", "fail");

                    request.getServletContext().log("Login failed");
                    responseJsonObject.addProperty("message", "ERROR: incorrect password");
                }
            }
            else {

                responseJsonObject.addProperty("status", "fail");

                request.getServletContext().log("Login failed");
                responseJsonObject.addProperty("message", "ERROR: user " + email + " doesn't exist");
            }

            rs1.close();
            statement1.close();

            out.write(responseJsonObject.toString());
            response.setStatus(200);

        }

        catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            response.setStatus(500);
        } finally {
            out.close();
        }

    }
}