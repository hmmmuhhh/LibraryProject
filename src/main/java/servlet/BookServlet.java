package servlet;

import model.Book;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet(name = "BookServlet", urlPatterns = "/books/*")
public class BookServlet extends HttpServlet {
    static final List<Book> books = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Books</title>");
        out.println("    <link rel=\"stylesheet\" href=\"styles.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Books</h1>");
        out.println("    <table>");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th>Code</th>");
        out.println("                <th>Title</th>");
        out.println("                <th>Author</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");

        // Dynamically generate book rows
        synchronized (books) {
            for (Book book : books) {
                out.println("            <tr>");
                out.println("                <td>" + book.getCode() + "</td>");
                out.println("                <td>" + book.getTitle() + "</td>");
                out.println("                <td>" + book.getAuthor() + "</td>");
                out.println("            </tr>");
            }
        }

        out.println("        </tbody>");
        out.println("    </table>");

        out.println("    <h2>Add Book</h2>");
        out.println("    <form action=\"/LibraryProject/books\" method=\"post\">");
        out.println("        <label for=\"code\">Code:</label>");
        out.println("        <input type=\"text\" id=\"code\" name=\"code\" required><br>");
        out.println("        <label for=\"title\">Title:</label>");
        out.println("        <input type=\"text\" id=\"title\" name=\"title\" required><br>");
        out.println("        <label for=\"author\">Author:</label>");
        out.println("        <input type=\"text\" id=\"author\" name=\"author\" required><br>");
        out.println("        <button type=\"submit\">Add Book</button>");
        out.println("    </form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get form parameters
        String code = request.getParameter("code");
        String title = request.getParameter("title");
        String author = request.getParameter("author");

        // Validate parameters
        if (code == null || title == null || author == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        // Add the new book to the list
        synchronized (books) {
            books.add(new Book(code, title, author, LocalDate.now()));
        }

        // Redirect to the book list page
        response.sendRedirect(request.getContextPath() + "/books");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String code = path.substring(1);

        Map<String, String> params = parseParams(request);
        String title = params.get("title");
        String author = params.get("author");

        synchronized (books) {
            Book book = books.stream().filter(b -> b.getCode().equals(code)).findFirst().orElse(null);
            if (book == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (title != null) book.setTitle(title);
            if (author != null) book.setAuthor(author);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String code = path.substring(1);

        synchronized (books) {
            boolean removed = books.removeIf(b -> b.getCode().equals(code));
            if (!removed) response.sendError(HttpServletResponse.SC_NOT_FOUND);
            else response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    private Map<String, String> parseParams(HttpServletRequest request) throws IOException {
        String body = request.getReader().lines().collect(java.util.stream.Collectors.joining());
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}