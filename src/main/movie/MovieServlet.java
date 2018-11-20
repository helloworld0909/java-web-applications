package main.movie;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet(name = "MovieServlet", urlPatterns = "/api/movie")
public class MovieServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");

        PrintWriter out = response.getWriter();

        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();

            String query = String.format(
                    "select * from movies as m, stars_in_movies as sim, stars as s where m.id = sim.movieId and sim.starId = s.id and m.id = \"%s\";", id);
            ResultSet resultSet = statement.executeQuery(query);

            resultSet.next();

            JsonObject obj = new JsonObject();

            obj.addProperty("movieId", resultSet.getString("movieId"));
            obj.addProperty("title", resultSet.getString("title"));
            obj.addProperty("year", resultSet.getString("year"));
            obj.addProperty("director", resultSet.getString("director"));

            JsonArray starList = new JsonArray();

            do {
                JsonObject star = new JsonObject();
                star.addProperty("starId", resultSet.getString("starId"));
                star.addProperty("name", resultSet.getString("name"));
                star.addProperty("birthYear", resultSet.getString("birthYear"));
                starList.add(star);
            } while (resultSet.next());

            obj.add("starList", starList);

            out.write(obj.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();

            JsonObject obj = new JsonObject();
            out.write(obj.toString());
            response.setStatus(500);
        }
        out.close();
    }
}
