package servlet;

import model.Borrowing;
import model.Book;
import model.Member;
import javax.servlet.*;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Borrowings</h1>");
        out.println("<table border='1'><tr><th>Book Code</th><th>Member ID</th><th>Borrow Date</th><th>Return Date</th></tr>");
        for (Borrowing b : borrowings) {
            out.println("<tr><td>" + b.getBookCode() + "</td><td>" + b.getMemberId() + "</td><td>" + b.getBorrowDate() + "</td><td>" + (b.getReturnDate() != null ? b.getReturnDate() : "Not Returned") + "</td></tr>");
        }
        out.println("</table>");
        out.println("<h2>Borrow Book</h2>");
        out.println("<form method='post'>");
        out.println("Book Code: <input type='text' name='bookCode' required><br>");
        out.println("Member ID: <input type='text' name='memberId' required><br>");
        out.println("<input type='submit' value='Borrow'></form>");
        out.println("<h2>Return Book</h2>");
        out.println("<form method='post' action='return'>");
        out.println("Book Code: <input type='text' name='bookCode' required><br>");
        out.println("<input type='submit' value='Return'></form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo() != null ? request.getPathInfo() : "";

        if (path.equals("/return")) {
            // Handle return
            String bookCode = request.getParameter("bookCode");
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
            String bookCode = request.getParameter("bookCode");
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