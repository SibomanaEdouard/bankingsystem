package Backend.Controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            if (email != null && password != null) {
                out.println("Your email is " + email + " and password is " + password);
            } else {
                out.println("Please provide all credentials to continue");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Sorry, something went wrong. Please try again later!");
        }
    }
}
