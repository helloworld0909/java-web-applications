package main.movie;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // set response mime type
        response.setContentType("text/html");

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();

        JsonArray returnData = new JsonArray();

        try {
            // Create a new connection to database
            Connection connection = dataSource.getConnection();

            // Declare a new statement
            Statement statement = connection.createStatement();

            String query = "select movies.id, movies.title, movies.year, movies.director " +
                    "from movies, ratings " +
                    "where movies.id = ratings.movieId order by ratings.rating desc limit 10";
            ResultSet resultSet = statement.executeQuery(query);

            // add table header row


            // add a row for every star result
            while (resultSet.next()) {
                // get a star from result set
                JsonObject movieObj = new JsonObject();
                movieObj.addProperty("movieId", resultSet.getString("id"));
                movieObj.addProperty("title", resultSet.getString("title"));
                movieObj.addProperty("year", resultSet.getInt("year"));
                movieObj.addProperty("director", resultSet.getString("director"));
                returnData.add(movieObj);
            }

            out.write(returnData.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
            JsonArray obj = new JsonArray();
            out.write(obj.toString());
            response.setStatus(500);
        }
        out.close();
    }
}
