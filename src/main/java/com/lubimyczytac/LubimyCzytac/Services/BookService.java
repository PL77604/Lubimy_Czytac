package com.lubimyczytac.LubimyCzytac.Services;

import com.lubimyczytac.LubimyCzytac.Models.Book;
import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserService userService;

    public Book addBook(String title, String author, String description,
                        MultipartFile coverImage, String link, String[] genres, User user) throws Exception {

        String coverImageUrl = null;
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Plik musi być obrazem!");
            }
            if (coverImage.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("Plik nie może przekraczać 5MB!");
            }
            coverImageUrl = cloudinaryService.uploadBookCover(coverImage, user.getId());
        }

        String genresString = genres != null ? String.join(",", genres) : "";

        Book book = new Book(title, author, description, coverImageUrl, link, genresString, user.getId());
        Book savedBook = bookRepository.save(book);

        userService.updateStatistics(user.getId(), 1, 0);

        return savedBook;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByUser(Long userId) {
        return bookRepository.findByUserId(userId);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> searchBooks(String search, String genre) {
        if (search != null && !search.isEmpty() && genre != null && !genre.isEmpty()) {
            return bookRepository.searchByTitleAndGenre(search, genre);
        } else if (search != null && !search.isEmpty()) {
            return bookRepository.searchByTitle(search);
        } else if (genre != null && !genre.isEmpty()) {
            return bookRepository.findByGenre(genre);
        }
        return bookRepository.findAll();
    }

    public void incrementDownloadCount(Long bookId) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setDownloadCount(book.getDownloadCount() + 1);
            bookRepository.save(book);

            if (book.getUserId() != null) {
                userService.updateStatistics(book.getUserId(), 0, 1);  // <-- Zwiększa pobrane użytkownika
            }
        });
    }

    // Zaktualizuj metodę deleteBook w BookService
    public boolean deleteBook(Long bookId, User currentUser) {
        Book book = getBookById(bookId);
        if (book != null) {
            if (currentUser.isAdmin() || book.getUserId().equals(currentUser.getId())) {

                if (book.getCoverImage() != null && book.getCoverImage().startsWith("http")) {
                    try {
                        cloudinaryService.deleteImage(book.getCoverImage());
                    } catch (Exception e) {
                        System.err.println("Nie udało się usunąć okładki: " + e.getMessage());
                    }
                }

                bookRepository.deleteById(bookId);
                if (!currentUser.isAdmin() || book.getUserId().equals(currentUser.getId())) {
                    userService.updateStatistics(book.getUserId(), -1, 0);
                }
                return true;
            }
        }
        return false;
    }

    // Dodaj tę metodę do BookService.java
    public List<Book> getLatestBooks(int limit) {
        return bookRepository.findAll().stream()
                .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }



}