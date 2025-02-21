package servlet;

import model.Member;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("    <title>Members</title>");
        out.println("    <link rel=\"stylesheet\" href=\"styles.css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Members</h1>");
        out.println("    <table>");
        out.println("        <thead>");
        out.println("            <tr>");
        out.println("                <th>ID</th>");
        out.println("                <th>Name</th>");
        out.println("                <th>Email</th>");
        out.println("            </tr>");
        out.println("        </thead>");
        out.println("        <tbody>");

        // Dynamically generate member rows
        synchronized (members) {
            for (Member member : members) {
                out.println("            <tr>");
                out.println("                <td>" + member.getId() + "</td>");
                out.println("                <td>" + member.getName() + "</td>");
                out.println("                <td>" + member.getEmail() + "</td>");
                out.println("            </tr>");
            }
        }

        out.println("        </tbody>");
        out.println("    </table>");

        out.println("    <h2>Add Member</h2>");
        out.println("    <form action=\"/LibraryProject/members\" method=\"post\">");
        out.println("        <label for=\"name\">Name:</label>");
        out.println("        <input type=\"text\" id=\"name\" name=\"name\" required><br>");
        out.println("        <label for=\"email\">Email:</label>");
        out.println("        <input type=\"email\" id=\"email\" name=\"email\" required><br>");
        out.println("        <button type=\"submit\">Add Member</button>");
        out.println("    </form>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
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