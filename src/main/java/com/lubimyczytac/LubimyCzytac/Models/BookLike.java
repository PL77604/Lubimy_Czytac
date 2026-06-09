package com.lubimyczytac.LubimyCzytac.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "book_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "user_id"})
})
public class BookLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}