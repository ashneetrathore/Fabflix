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
import java.sql.*;

@WebServlet(name = "MetadataServlet", urlPatterns = "/dashboard/metadata")
public class MetadataServlet extends HttpServlet {
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

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            Statement statement1 = conn.createStatement();

            String query1 = "SHOW TABLES";

            ResultSet rs1 = statement1.executeQuery(query1);

            JsonArray metadataArray = new JsonArray();
            while (rs1.next()) {
                JsonObject tableObj = new JsonObject();
                String table_name = rs1.getString(1);
                tableObj.addProperty("table_name", table_name);

                JsonArray colArray = new JsonArray();
                ResultSet rs2 = metaData.getColumns(null, null, table_name, "%");
                while (rs2.next()) {
                    JsonObject columnObj = new JsonObject();
                    String col_name = rs2.getString("COLUMN_NAME");
                    String col_type = rs2.getString("TYPE_NAME");

                    columnObj.addProperty("col_name", col_name);
                    columnObj.addProperty("col_type", col_type);

                    colArray.add(columnObj);
                }
                tableObj.add("columns", colArray);

                metadataArray.add(tableObj);

            }
            rs1.close();
            statement1.close();

            request.getServletContext().log("getting " + metadataArray.size() + " results");

            out.write(metadataArray.toString());
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
