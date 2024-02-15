package Backend.Controllers;

import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import Backend.DbConnection.DbConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/withdraw")
public class WithdrawController extends HttpServlet {

    Connection connection;

    @Override
    public void init() {
        // Initialize the connection object
        try {
            connection = DbConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Check if the bank table exists, and create it if it doesn't
        createBankTable();
    }

    // This method creates the bank table if it doesn't exist
    private void createBankTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS bank (" +
                    "email varchar(255) not null," +
                    "amount varchar(255) not null," +
                    "last_withdraw_time TIMESTAMP DEFAULT NULL," +
                    "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            statement.executeUpdate(query);
            System.out.println("Table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");
        if (email == null) {
            out.println("User not logged in.");
            res.sendRedirect("./index.jsp");
        }

        String amountStr = req.getParameter("amount");
        if (amountStr == null || amountStr.isEmpty()) {
            out.println("Amount not provided.");
            res.sendRedirect("./deposit.jsp");
        }
        double amount = Double.parseDouble(amountStr);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Retrieve the old amount from the database
            String selectQuery = "SELECT amount FROM bank WHERE email = ?";
            preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            double remainingAmount = 0.0;
            if (resultSet.next()) {
                // If the email exists in the database, retrieve the old amount
                double oldAmount = resultSet.getDouble("amount");
                // Check if there's enough balance for withdrawal
                if (oldAmount < amount) {
                    out.println("Insufficient balance.");
                    return;
                }
                // Calculate the remaining amount after withdrawal
                remainingAmount = oldAmount - amount;

                // Update the amount in the database
                String updateQuery = "UPDATE bank SET amount = ? WHERE email = ?";
                preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setDouble(1, remainingAmount);
                preparedStatement.setString(2, email);
                preparedStatement.executeUpdate();
            } else {
                // If the email does not exist in the database, return an error
                out.println("You don't have any money in your account!");
                return;
            }

            // Print the remaining and withdrawn amounts
            out.println("Withdrawal successful.");
            out.println("Remaining amount: " + remainingAmount);
            out.println("Withdrawn amount: " + amount);

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error occurred. Please try again later.");
        } finally {
            // Close the database resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
