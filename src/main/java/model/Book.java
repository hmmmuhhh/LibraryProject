package model;

import java.time.LocalDate;

public class Book {
    private final String code;
    private String title;
    private String author;
    private final LocalDate date;

    public Book(String code, String title, String author, LocalDate date) {
        this.code = code;
        this.title = title;
        this.author = author;
        this.date = date;
    }

    // Getters and Setters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public LocalDate getDate() { return date; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
}