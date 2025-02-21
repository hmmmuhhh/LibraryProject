package servlet;

import model.Member;
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

@WebServlet(name = "MemberServlet", urlPatterns = "/members/*")
public class MemberServlet extends HttpServlet {
    static final List<Member> members = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Members</h1>");
        out.println("<table border='1'><tr><th>ID</th><th>Name</th><th>Email</th><th>Join Date</th></tr>");
        for (Member member : members) {
            out.println("<tr><td>" + member.getId() + "</td><td>" + member.getName() + "</td><td>" + member.getEmail() + "</td><td>" + member.getJoinDate() + "</td></tr>");
        }
        out.println("</table>");
        out.println("<h2>Add New Member</h2>");
        out.println("<form method='post'>");
        out.println("Name: <input type='text' name='name' required><br>");
        out.println("Email: <input type='email' name='email' required><br>");
        out.println("<input type='submit' value='Add Member'></form>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        if (name == null || email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        synchronized (members) {
            members.add(new Member(name, email, LocalDate.now()));
        }
        response.sendRedirect(request.getContextPath() + "/members");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null || path.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String id = path.substring(1);

        Map<String, String> params = parseParams(request);
        String name = params.get("name");
        String email = params.get("email");

        synchronized (members) {
            Member member = members.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
            if (member == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (name != null) member.setName(name);
            if (email != null) member.setEmail(email);
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
        String id = path.substring(1);

        synchronized (members) {
            boolean removed = members.removeIf(m -> m.getId().equals(id));
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