package Backend.Filters;

import  java.util.regex.*;
import Backend.DbConnection.DbConnection;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebFilter("/register") // Specify the URL pattern to which this filter should be applied
public class RegisterUserFilters implements Filter {
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
        String phone = request.getParameter("phone");
        int age = Integer.parseInt(request.getParameter("age"));
        String password=request.getParameter("password");


        //this is validation of password with regex

        try {
            // Check if the email is already in the database
            PreparedStatement emailCheckStmt = connection.prepareStatement("SELECT email FROM users WHERE email = ?");
            emailCheckStmt.setString(1, email);
            ResultSet emailResult = emailCheckStmt.executeQuery();

            PreparedStatement phoneCheck=connection.prepareStatement("SELECT phone FROM users WHERE phone=?");
            phoneCheck.setString(1,phone);
            ResultSet phoneResult=phoneCheck.executeQuery();

            if (emailResult.next() || phoneResult.next()) {
                response.getWriter().println("Email or phonenumber  already exists in the database. Please use a different email or phone.");
            } else if (age < 18) {
                response.getWriter().println("Sorry! You are under 18 and you can't use our system.");
            }else if(!isValidPassword(password)){
                response.getWriter().println("Password must contain atleast one lower , upper ,number , special character and its size must be between 8 and 20");

            }
            else {
                // If email is not in the database and age is valid, proceed with the chain
                chain.doFilter(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred. Please try again later.");
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


//this is to validate password

    public static boolean isValidPassword(String pass){
        //this to check regex
        String regex= "^(?=.*[0-9])"
                + "(?=.*[a-z])"
                + "(?=.*[#$%&=*]).{8,20}$";

        //this is to compile the inserted password
        Pattern p=Pattern.compile(regex);
        if(pass==null){
            return false;
        }
        Matcher m=p.matcher(pass);
        return  m.matches();
    }
}

