package com.lubimyczytac.LubimyCzytac.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_book_lists", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id", "list_type"})
})
public class UserBookList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "list_type", nullable = false)
    private String listType;

    @Column(name = "status")
    private String status;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    public UserBookList() {
        this.addedAt = LocalDateTime.now();
        this.status = "WANT_TO_READ";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}