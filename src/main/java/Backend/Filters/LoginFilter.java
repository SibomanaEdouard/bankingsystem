package Backend.Filters;

import Backend.DbConnection.DbConnection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebFilter("/login")
public class LoginFilter implements Filter {
    Connection connection;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            // Initialize database connection
            connection = DbConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        PrintWriter out=response.getWriter();


        try {
            // Check if the email is already in the database
            PreparedStatement user = connection.prepareStatement("SELECT * FROM users WHERE email = ?");
            user.setString(1, email);
            ResultSet userResult = user.executeQuery();

            if (userResult.next()) {
                String hashedPasswordFromDB = userResult.getString("password");

                // Check if the hashed password from the database matches the entered password
                if (BCrypt.checkpw(password, hashedPasswordFromDB)) {
                    //this is to save email in the session
                    HttpSession session = ((HttpServletRequest) request).getSession();
                    session.setAttribute("email",email);
                    // Passwords match, proceed with authentication
                    chain.doFilter(request, response);
                } else {
                    // Passwords don't match, send error message
                    out.println("Invalid email or password!");
                }
            } else {
                // No user found with the given email, send error message
                out.println("Invalid email or password!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred. Please try again later.");
            System.out.println("Login Filter: Error occurred while processing request.");
        }
    }

    @Override
    public void destroy() {
        // Close database connection
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
