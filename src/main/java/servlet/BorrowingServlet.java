package servlet;

import model.Borrowing;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "BorrowingServlet", urlPatterns = "/borrow/*")
public class BorrowingServlet extends HttpServlet {
    private static final List<Borrowing> borrowings = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Borrowings</title>");
        out.println("    <link rel=\"stylesheet\" href=\"styles.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Borrowings</h1>");
        out.println("    <table>");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th>Book Code</th>");
        out.println("                <th>Member ID</th>");
        out.println("                <th>Borrow Date</th>");
        out.println("                <th>Return Date</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");

        synchronized (borrowings) {
            for (Borrowing borrowing : borrowings) {
                out.println("            <tr>");
                out.println("                <td>" + borrowing.getBookCode() + "</td>");
                out.println("                <td>" + borrowing.getMemberId() + "</td>");
                out.println("                <td>" + borrowing.getBorrowDate() + "</td>");
                out.println("                <td>" + (borrowing.getReturnDate() != null ? borrowing.getReturnDate() : "Not Returned") + "</td>");
                out.println("            </tr>");
            }
        }

        out.println("        </tbody>");
        out.println("    </table>");

        out.println("    <h2>Borrow Book</h2>");
        out.println("    <form action=\"/LibraryProject/borrow\" method=\"post\">");
        out.println("        <label for=\"bookCode\">Book Code:</label>");
        out.println("        <input type=\"text\" id=\"bookCode\" name=\"bookCode\" required><br>");
        out.println("        <label for=\"memberId\">Member ID:</label>");
        out.println("        <input type=\"text\" id=\"memberId\" name=\"memberId\" required><br>");
        out.println("        <button type=\"submit\">Borrow</button>");
        out.println("    </form>");

        out.println("    <h2>Return Book</h2>");
        out.println("    <form action=\"/LibraryProject/borrow/return\" method=\"post\">");
        out.println("        <label for=\"returnBookCode\">Book Code:</label>");
        out.println("        <input type=\"text\" id=\"returnBookCode\" name=\"bookCode\" required><br>");
        out.println("        <button type=\"submit\">Return</button>");
        out.println("    </form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";

        String bookCode = request.getParameter("bookCode");
        if (path.equals("/return")) {
            // Handle return
            synchronized (borrowings) {
                Borrowing borrowing = borrowings.stream()
                        .filter(b -> b.getBookCode().equals(bookCode) && b.getReturnDate() == null)
                        .findFirst()
                        .orElse(null);
                if (borrowing == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Book not borrowed");
                    return;
                }
                borrowing.setReturnDate(LocalDate.now());
            }
        } else {
            // Handle borrow
            String memberId = request.getParameter("memberId");

            boolean bookExists = BookServlet.books.stream().anyMatch(b -> b.getCode().equals(bookCode));
            boolean memberExists = MemberServlet.members.stream().anyMatch(m -> m.getId().equals(memberId));

            if (!bookExists || !memberExists) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid book or member");
                return;
            }

            synchronized (borrowings) {
                boolean alreadyBorrowed = borrowings.stream()
                        .anyMatch(b -> b.getBookCode().equals(bookCode) && b.getReturnDate() == null);
                if (alreadyBorrowed) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Book already borrowed");
                    return;
                }
                borrowings.add(new Borrowing(bookCode, memberId, LocalDate.now(), null));
            }
        }

        response.sendRedirect(request.getContextPath() + "/borrow");
    }
}