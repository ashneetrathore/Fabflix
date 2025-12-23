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
import java.util.ArrayList;

@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            String queryCount = "SELECT COUNT(*) AS movieCount";

            String queryColumns = "SELECT m.id,m.title, m.year, m.director, r.rating";

            String queryp1 = " FROM movies AS m" +
                            " LEFT JOIN ratings AS r ON r.movieId = m.id" +
                            " WHERE ";
            String queryp2 = "";
            String queryp3 = "ORDER BY m.title ASC, r.rating ASC";
            String queryp4 = " LIMIT 10";
            String queryp5 = "";

            String genre = request.getParameter("genre");
            String firstChar = request.getParameter("firstChar");
            String orderBy = request.getParameter("orderBy");
            String perPage = request.getParameter("perPage");
            String pageNum = request.getParameter("pageNum");

            Integer perPageInt = 10;
            Integer pageInt = 1;

            ArrayList<Object> placeholders = new ArrayList<>();

            if (genre != null && !genre.isEmpty()) {
                queryp2 = "m.id IN (SELECT gim.movieId" +
                            " FROM genres_in_movies AS gim" +
                            " JOIN (SELECT id" +
                            " FROM genres" +
                            " WHERE name = ?) AS g on g.id = gim.genreId)";
                placeholders.add(genre);
            }
            else if (firstChar != null && !firstChar.isEmpty()) {
                if ("*".equals(firstChar)) {
                    queryp2 = "title REGEXP '^[^A-Za-z0-9]'";
                } else {
                    queryp2 = "title LIKE ?";
                    placeholders.add(firstChar + "%");
                }
            }
            else {
                String title = request.getParameter("title");
                String year = request.getParameter("year");
                String director = request.getParameter("director");
                String star_name = request.getParameter("star_name");

                ArrayList<String> queryConditions = new ArrayList<>();

                if (title != null && !title.isEmpty()) {
                    String fullTextTitle = "";
                    String[] keywords = title.split(" ");
                    for (String keyword : keywords) {
                        keyword = keyword.trim();
                        if (!keyword.isEmpty()) {
                            fullTextTitle += "+" + keyword + "* ";
                        }
                    }
                    fullTextTitle = fullTextTitle.trim();
                    queryConditions.add("MATCH (title) AGAINST (? IN BOOLEAN MODE)");
                    placeholders.add(fullTextTitle);
                }
                if (year != null && !year.isEmpty()) {
                    queryConditions.add("year = ?");
                    Integer movieYearInt = Integer.parseInt(year);
                    placeholders.add(movieYearInt);
                }
                if (director != null && !director.isEmpty()) {
                    queryConditions.add("director LIKE ?");
                    placeholders.add("%" + director + "%");
                }
                if (star_name != null && !star_name.isEmpty()) {
                    String matchStarName = "m.id IN (SELECT sim.movieId" +
                                            " FROM stars_in_movies AS sim" +
                                            " JOIN (SELECT id" +
                                            " FROM stars" +
                                            " WHERE name LIKE ?) AS s on s.id = sim.starId)";
                    queryConditions.add(matchStarName);
                    placeholders.add("%" + star_name + "%");
                }
                queryp2 = String.join(" AND ", queryConditions);
            }

            if (orderBy != null && !orderBy.isEmpty()) {
                switch (orderBy) {
                    case "tArA":
                        queryp3 = " ORDER BY m.title ASC, r.rating ASC";
                        break;
                    case "tDrD":
                        queryp3 = " ORDER BY m.title DESC, r.rating DESC";
                        break;
                    case "tArD":
                        queryp3 = " ORDER BY m.title ASC, r.rating DESC";
                        break;
                    case "tDrA":
                        queryp3 = " ORDER BY m.title DESC, r.rating ASC";
                        break;
                    case "rAtA":
                        queryp3 = " ORDER BY r.rating ASC, m.title ASC";
                        break;
                    case "rDtD":
                        queryp3 = " ORDER BY r.rating DESC, m.title DESC";
                        break;
                    case "rAtD":
                        queryp3 = " ORDER BY r.rating ASC, m.title DESC";
                        break;
                    case "rDtA":
                        queryp3 = " ORDER BY r.rating DESC, m.title ASC";
                        break;
                    default:
                        break;
                }
            }

            if (perPage != null && !perPage.isEmpty()) {
                perPageInt = Integer.parseInt(perPage);
                queryp4 = " LIMIT " + perPageInt;
            }

            if (pageNum != null && !pageNum.isEmpty()) {
                pageInt = Integer.parseInt(pageNum);
            }
            int offset = (pageInt - 1) * perPageInt;
            queryp5 = " OFFSET " + offset;

            String completeQuery1 = queryColumns + queryp1 + queryp2 + queryp3 + queryp4 + queryp5;
            String completeQuery2 = queryCount + queryp1 + queryp2;

            PreparedStatement statement1 = conn.prepareStatement(completeQuery1);
            PreparedStatement countStatement = conn.prepareStatement(completeQuery2);

            for (int i = 0; i < placeholders.size(); i++) {
                statement1.setObject(i + 1, placeholders.get(i));
                countStatement.setObject(i + 1, placeholders.get(i));
            }

            ResultSet rs1 = statement1.executeQuery();
            ResultSet countOfMovies = countStatement.executeQuery();

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
                                " GROUP BY starId) DESC, s.name ASC" +
                                " LIMIT 3";

                PreparedStatement statement2 = conn.prepareStatement(query2);

                statement2.setString(1, movie_id);

                ResultSet rs2 = statement2.executeQuery();

                StringBuilder threeStars = new StringBuilder();
                StringBuilder threeStarIds = new StringBuilder();
                int i = 0;
                while (rs2.next()) {
                    if (i > 0) threeStars.append(",");
                    if (i > 0) threeStarIds.append(",");
                    threeStars.append(rs2.getString("name"));
                    threeStarIds.append(rs2.getString("id"));
                    i++;
                }

                String query3 = "SELECT g.name" +
                                " FROM genres AS g" +
                                " JOIN (SELECT genreId" +
                                " FROM genres_in_movies" +
                                " WHERE movieId = ?) AS gim ON g.id = gim.genreId" +
                                " ORDER BY g.name" +
                                " LIMIT 3";

                PreparedStatement statement3 = conn.prepareStatement(query3);

                statement3.setString(1, movie_id);

                ResultSet rs3 = statement3.executeQuery();

                StringBuilder threeGenres = new StringBuilder();
                i = 0;
                while (rs3.next()) {
                    if (i > 0) threeGenres.append(",");
                    threeGenres.append(rs3.getString("name"));
                    i++;
                }

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_name", movie_name);
                jsonObject.addProperty("movie_yr", movie_yr);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", threeGenres.toString());
                jsonObject.addProperty("movie_stars", threeStars.toString());
                jsonObject.addProperty("movie_star_ids", threeStarIds.toString());
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);

                rs2.close();
                rs3.close();
                statement2.close();
                statement3.close();
            }

            while (countOfMovies.next()) {
                String totalMovies = countOfMovies.getString("movieCount");

                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("totalMovies", totalMovies);

                jsonArray.add(jsonObject2);
            }
            rs1.close();
            statement1.close();

            countOfMovies.close();
            countStatement.close();

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
