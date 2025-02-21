package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HomeServlet", urlPatterns = "/")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Library Home</title>");
        out.println("    <style>");
        out.println("        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; }");
        out.println("        h3 { margin-bottom: 20px; }");
        out.println("        .button { display: inline-block; margin: 10px; padding: 10px 20px; font-size: 16px; ");
        out.println("                 color: white; background-color: #007BFF; text-decoration: none; border-radius: 5px; }");
        out.println("        .button:hover { background-color: #0056b3; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h3>Welcome! Please use these buttons to access our library &lt;3</h3>");
        out.println("    <a href=\"/LibraryProject/books\" class=\"button\">Books</a>");
        out.println("    <a href=\"/LibraryProject/members\" class=\"button\">Members</a>");
        out.println("    <a href=\"/LibraryProject/borrow\" class=\"button\">Borrow</a>");
        out.println("</body>");
        out.println("</html>");
    }
}