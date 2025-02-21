package model;

import java.time.LocalDate;

public class Borrowing {
    private final String bookCode;
    private final String memberId;
    private final LocalDate borrowDate;
    private LocalDate returnDate;

    public Borrowing(String bookCode, String memberId, LocalDate borrowDate, LocalDate returnDate) {
        this.bookCode = bookCode;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public String getBookCode() { return bookCode; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}