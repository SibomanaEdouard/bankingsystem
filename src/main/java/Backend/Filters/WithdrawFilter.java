package Backend.Filters;

import Backend.DbConnection.DbConnection;
import jakarta.servlet.*;
        import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
@WebFilter("/withdraw")
public class WithdrawFilter implements Filter {

    Connection connection;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialize database connection
        try {
            connection = DbConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");
        if (email == null) {
            res.sendRedirect("./index.jsp");
            return;
        }

        try {
            // Check if the user has already made a withdrawal this month
            if (!hasWithdrawnThisMonth(email)) {
                // If not, allow the request to proceed
                chain.doFilter(request, response);
            } else {
                // If yes, redirect the user with a message indicating that they can't withdraw again this month
                res.sendRedirect("./withdraw.jsp?message=You%20have%20already%20made%20a%20withdrawal%20this%20month.");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
//            res.sendRedirect("./error.jsp");
        }
    }

    private boolean hasWithdrawnThisMonth(String email) throws SQLException {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Get the first day of the current month
        LocalDate firstDayOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);

        // Convert the first day of the month to a LocalDateTime object with time set to 00:00:00
        LocalDateTime firstDayOfMonthDateTime = firstDayOfMonth.atStartOfDay();

        // Query to check if the user has made a withdrawal this month
        String query = "SELECT COUNT(*) FROM bank WHERE email = ? AND last_withdraw_time >= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            // Convert LocalDateTime to SQL TIMESTAMP and set it as a parameter
            preparedStatement.setTimestamp(2, Timestamp.valueOf(firstDayOfMonthDateTime));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // If count > 0, the user has made a withdrawal this month
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    @Override
    public void destroy() {
        // Close database connection
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
