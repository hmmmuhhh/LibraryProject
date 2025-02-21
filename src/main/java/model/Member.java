package model;

import java.time.LocalDate;
import java.util.UUID;

public class Member {
    private final String id;
    private String name;
    private String email;
    private final LocalDate joinDate;

    public Member(String name, String email, LocalDate joinDate) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getJoinDate() { return joinDate; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}