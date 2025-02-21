package servlet;

import model.Book;
import javax.servlet.*;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Books</h1>");
        out.println("<table border='1'><tr><th>Code</th><th>Title</th><th>Author</th><th>Date Added</th></tr>");
        for (Book book : books) {
            out.println("<tr><td>" + book.getCode() + "</td><td>" + book.getTitle() + "</td><td>" + book.getAuthor() + "</td><td>" + book.getDate() + "</td></tr>");
        }
        out.println("</table>");
        out.println("<h2>Add New Book</h2>");
        out.println("<form method='post'>");
        out.println("Code: <input type='text' name='code' required><br>");
        out.println("Title: <input type='text' name='title' required><br>");
        out.println("Author: <input type='text' name='author' required><br>");
        out.println("<input type='submit' value='Add Book'></form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String title = request.getParameter("title");
        String author = request.getParameter("author");

        if (code == null || title == null || author == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        synchronized (books) {
            boolean exists = books.stream().anyMatch(b -> b.getCode().equals(code));
            if (exists) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Book code already exists");
                return;
            }
            books.add(new Book(code, title, author, LocalDate.now()));
        }
        response.sendRedirect(request.getContextPath() + "/books");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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