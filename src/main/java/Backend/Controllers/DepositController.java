package Backend.Controllers;//package Backend.Controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import Backend.DbConnection.DbConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/deposit")
public class DepositController extends HttpServlet {

    Connection connection;


    @Override
    public void init() {
        // Initialize the connection object
        try {
            connection = DbConnection.getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Create the bank table
        createBankTable();
    }

    //this is to create the bank table
    private void createBankTable() {
        try (Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS bank (" +
                    "email varchar(255) not null," +
                    "amount varchar(255) not null)";
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
            return;
        }

        String amountStr = req.getParameter("amount");
        if (amountStr == null || amountStr.isEmpty()) {
            out.println("Amount not provided.");
            return;
        }
        double amount = Double.parseDouble(amountStr);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DbConnection.getConnection();

            // Retrieve the old amount from the database
            String selectQuery = "SELECT amount FROM bank WHERE email = ?";
            preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            double totalAmount = amount;
            if (resultSet.next()) {
                // If the email exists in the database, add the new amount to the old amount
                double oldAmount = resultSet.getDouble("amount");
                totalAmount += oldAmount;

                // Update the amount in the database
                String updateQuery = "UPDATE bank SET amount = ? WHERE email = ?";
                preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setDouble(1, totalAmount);
                preparedStatement.setString(2, email);
                preparedStatement.executeUpdate();
            } else {
                // If the email does not exist, insert a new row into the table
                String insertQuery = "INSERT INTO bank (email, amount) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, email);
                preparedStatement.setDouble(2, totalAmount);
                preparedStatement.executeUpdate();
            }

            out.println("Deposit successful. Total amount: " + totalAmount);

        } catch (SQLException | ClassNotFoundException e) {
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
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

