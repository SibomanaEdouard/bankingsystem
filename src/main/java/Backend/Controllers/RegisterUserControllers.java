package Backend.Controllers;

import Backend.DbConnection.DbConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;
import Backend.Models.Users;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/register")
public class RegisterUserControllers extends HttpServlet {
    Connection connection = DbConnection.getConnection();

    public RegisterUserControllers() throws SQLException, ClassNotFoundException {
    }

    @Override
    public void init() throws ServletException {
        super.init();
        createClientTable();
    }

    //this is to create table
    public void createClientTable() {
        try (Statement statement = connection.createStatement()) {

            String query = "CREATE TABLE IF NOT EXISTS users (" +
                    "username varchar(255) not null," +
                    "email varchar(255) not null," +
                    "phone varchar(255) not null," +
                    "password varchar(255) not null," +
                    "age varchar(255) not null" + // Assuming age is an integer
                    ")";
            statement.executeUpdate(query);
            System.out.println("Table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // Send notification method
    private void sendNotification(String message, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type=\"text/javascript\">");
        out.println("alert('" + message + "');");
        out.println("window.location.href='index.jsp';");
        out.println("</script>");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        // this is to get input from user
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String age = req.getParameter("age");
        String password = req.getParameter("password");
        try {

            // this is to check if the user inserted inputs
            if (username != null &&
                    email != null &&
                    phone != null &&
                    age != null &&
                    password != null) {
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                Users users = new Users();
                users.setAge(age);
                users.setPassword(hashedPassword);
                users.setEmail(email);
                users.setPhone(phone);
                users.setUsername(username);

                // this is to save the user to the database
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username,email,password,phone,age)VALUES (?,?,?,?,?)");
                preparedStatement.setString(1, users.getUsername());
                preparedStatement.setString(2, users.getEmail());
                preparedStatement.setString(3, users.getPassword());
                preparedStatement.setString(4, users.getPhone());
                preparedStatement.setString(5, users.getAge());
                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    String message = "Your Registration to Bankw is successful!";
                    sendNotification(message, res);
                } else {
                    out.println("Failed to register in Bankw");
                }

            } else {
                out.println("Please ! All credentials are required to continue");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            out.println("Sorry something went wrong please Try again later!");
        }
    }
}
