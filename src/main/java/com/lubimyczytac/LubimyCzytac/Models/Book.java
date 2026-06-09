package com.lubimyczytac.LubimyCzytac.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(length = 2000)
    private String description;

    @Column(name = "cover_image")
    private String coverImage;

    @Column
    private String link;

    @Column(name = "genres")
    private String genres;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "download_count")
    private Integer downloadCount;

    @Transient
    private Integer likesCount = 0;

    @Transient
    private Integer commentsCount = 0;

    @Transient
    private boolean userLiked = false;

    public Book() {
        this.createdAt = LocalDateTime.now();
        this.downloadCount = 0;
    }

    public Book(String title, String author, String description, String coverImage, String link, String genres, Long userId) {
        this();
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
        this.link = link;
        this.genres = genres;
        this.userId = userId;
    }

    public String[] getGenresArray() {
        if (genres == null || genres.isEmpty()) return new String[0];
        return Arrays.stream(genres.split(","))
                .map(String::trim)
                .filter(g -> !g.isEmpty())
                .distinct()
                .toArray(String[]::new);
    }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public boolean isUserLiked() { return userLiked; }
    public void setUserLiked(boolean userLiked) { this.userLiked = userLiked; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }
}