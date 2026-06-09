package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.Book;
import com.lubimyczytac.LubimyCzytac.Models.BookLike;
import com.lubimyczytac.LubimyCzytac.Models.Comment;
import com.lubimyczytac.LubimyCzytac.Models.User;
import com.lubimyczytac.LubimyCzytac.Models.UserBookHistory;
import com.lubimyczytac.LubimyCzytac.Models.UserBookList;
import com.lubimyczytac.LubimyCzytac.Repositories.BookLikeRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.CommentRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.UserBookHistoryRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.UserBookListRepository;
import com.lubimyczytac.LubimyCzytac.Services.BookService;
import com.lubimyczytac.LubimyCzytac.Services.UserService;
import com.lubimyczytac.LubimyCzytac.Services.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private UserBookHistoryRepository userBookHistoryRepository;

    @Autowired
    private UserBookListRepository userBookListRepository;

    @GetMapping("/DodanieKsiazek")
    public String dodanie(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }
        return "dodanie";
    }

    @GetMapping("/Katalog/{id}")
    public String bookDetails(@PathVariable Long id, Model model, HttpSession session) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/Katalog";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            saveBookHistory(loggedUser.getId(), id);

            boolean isReading = userBookListRepository.findByUserIdAndBookIdAndListType(
                    loggedUser.getId(), id, "READING").isPresent();
            boolean isFavorite = userBookListRepository.findByUserIdAndBookIdAndListType(
                    loggedUser.getId(), id, "FAVORITE").isPresent();
            boolean isMyBook = userBookListRepository.findByUserIdAndBookIdAndListType(
                    loggedUser.getId(), id, "MY_BOOKS").isPresent();

            model.addAttribute("isReading", isReading);
            model.addAttribute("isFavorite", isFavorite);
            model.addAttribute("isMyBook", isMyBook);
        }

        int likesCount = bookLikeRepository.countByBookId(id);
        int commentsCount = commentRepository.countByBookId(id);
        book.setLikesCount(likesCount);
        book.setCommentsCount(commentsCount);

        if (loggedUser != null) {
            boolean userLiked = bookLikeRepository.findByBookIdAndUserId(id, loggedUser.getId()).isPresent();
            book.setUserLiked(userLiked);
        }

        List<Comment> comments = commentRepository.findByBookIdOrderByCreatedAtDesc(id);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("canDeleteBook", canDeleteBook(loggedUser, book));
        return "ksiazka";
    }

    private void saveBookHistory(Long userId, Long bookId) {
        Optional<UserBookHistory> existing = userBookHistoryRepository.findByUserIdAndBookId(userId, bookId);

        if (existing.isPresent()) {
            UserBookHistory history = existing.get();
            history.setLastViewed(LocalDateTime.now());
            history.setViewCount(history.getViewCount() + 1);
            userBookHistoryRepository.save(history);
        } else {
            UserBookHistory history = new UserBookHistory();
            history.setUserId(userId);
            history.setBookId(bookId);
            userBookHistoryRepository.save(history);
        }
    }

    private boolean canDeleteBook(User user, Book book) {
        if (user == null) return false;
        return user.isAdmin() || (book.getUserId() != null && book.getUserId().equals(user.getId()));
    }

    @GetMapping("/Katalog")
    public String katalog(Model model,
                          @RequestParam(value = "szukaj", required = false) String search,
                          @RequestParam(value = "gatunek", required = false) String genre,
                          HttpSession session) {

        List<Book> books;
        List<Book> recentBooks;
        List<Book> recommendedBooks;
        boolean isSearching = (search != null && !search.isEmpty()) || (genre != null && !genre.isEmpty());

        if (!isSearching) {
            books = bookService.getAllBooks();
        } else {
            books = bookService.searchBooks(search, genre);
        }

        if (!isSearching) {
            recentBooks = bookService.getAllBooks().stream()
                    .sorted((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()))
                    .limit(5)
                    .collect(Collectors.toList());
        } else {
            recentBooks = new ArrayList<>();
        }

        if (!isSearching) {
            recommendedBooks = bookService.getAllBooks().stream()
                    .sorted((b1, b2) -> {
                        int likes1 = bookLikeRepository.countByBookId(b1.getId());
                        int likes2 = bookLikeRepository.countByBookId(b2.getId());
                        return Integer.compare(likes2, likes1);
                    })
                    .limit(5)
                    .collect(Collectors.toList());
        } else {
            recommendedBooks = new ArrayList<>();
        }

        for (Book book : books) {
            int likesCount = bookLikeRepository.countByBookId(book.getId());
            int commentsCount = commentRepository.countByBookId(book.getId());
            book.setLikesCount(likesCount);
            book.setCommentsCount(commentsCount);
        }

        for (Book book : recentBooks) {
            int likesCount = bookLikeRepository.countByBookId(book.getId());
            int commentsCount = commentRepository.countByBookId(book.getId());
            book.setLikesCount(likesCount);
            book.setCommentsCount(commentsCount);
        }

        for (Book book : recommendedBooks) {
            int likesCount = bookLikeRepository.countByBookId(book.getId());
            int commentsCount = commentRepository.countByBookId(book.getId());
            book.setLikesCount(likesCount);
            book.setCommentsCount(commentsCount);
        }

        model.addAttribute("books", books);
        model.addAttribute("recentBooks", recentBooks);
        model.addAttribute("recommendedBooks", recommendedBooks);
        model.addAttribute("searchTerm", search);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("isSearching", isSearching);
        model.addAttribute("hasResults", books != null && !books.isEmpty());
        return "katalog";
    }

    @GetMapping("/MojeKsiazki")
    public String myBooks(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        List<UserBookList> readingList = userBookListRepository.findByUserIdAndListTypeOrderByAddedAtDesc(
                loggedUser.getId(), "READING");
        List<UserBookList> favoriteList = userBookListRepository.findByUserIdAndListTypeOrderByAddedAtDesc(
                loggedUser.getId(), "FAVORITE");

        List<UserBookList> myBooksList = userBookListRepository.findByUserIdAndListTypeOrderByAddedAtDesc(
                loggedUser.getId(), "MY_BOOKS");

        List<UserBookHistory> history = userBookHistoryRepository.findByUserIdOrderByLastViewedDesc(loggedUser.getId());

        List<Book> readingBooks = new ArrayList<>();
        for (UserBookList item : readingList) {
            Book book = bookService.getBookById(item.getBookId());
            if (book != null) readingBooks.add(book);
        }

        List<Book> favoriteBooks = new ArrayList<>();
        for (UserBookList item : favoriteList) {
            Book book = bookService.getBookById(item.getBookId());
            if (book != null) favoriteBooks.add(book);
        }

        List<Book> mySavedBooks = new ArrayList<>();
        for (UserBookList item : myBooksList) {
            Book book = bookService.getBookById(item.getBookId());
            if (book != null) mySavedBooks.add(book);
        }

        List<Book> historyBooks = new ArrayList<>();
        for (UserBookHistory item : history) {
            Book book = bookService.getBookById(item.getBookId());
            if (book != null) historyBooks.add(book);
        }

        model.addAttribute("readingBooks", readingBooks);
        model.addAttribute("favoriteBooks", favoriteBooks);
        model.addAttribute("userAddedBooks", mySavedBooks);
        model.addAttribute("historyBooks", historyBooks);

        return "moje-ksiazki";
    }

    @PostMapping("/api/books/{id}/add-to-list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBookToList(
            @PathVariable Long id,
            @RequestParam String listType,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Optional<UserBookList> existing = userBookListRepository.findByUserIdAndBookIdAndListType(
                loggedUser.getId(), id, listType);

        if (existing.isPresent()) {
            response.put("success", false);
            response.put("message", "Książka już jest na tej liście!");
            return ResponseEntity.ok(response);
        }

        UserBookList userBook = new UserBookList();
        userBook.setUserId(loggedUser.getId());
        userBook.setBookId(id);
        userBook.setListType(listType);
        userBookListRepository.save(userBook);

        response.put("success", true);
        response.put("message", "Książka dodana do listy!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/books/{id}/remove-from-list")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeBookFromList(
            @PathVariable Long id,
            @RequestParam String listType,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        userBookListRepository.deleteByUserIdAndBookIdAndListType(loggedUser.getId(), id, listType);

        if ("FAVORITE".equals(listType)) {
            Optional<BookLike> existingLike = bookLikeRepository.findByBookIdAndUserId(id, loggedUser.getId());
            if (existingLike.isPresent()) {
                bookLikeRepository.delete(existingLike.get());
                System.out.println("Usunięto polubienie książki ID: " + id + " dla użytkownika: " + loggedUser.getId());
            }
        }

        response.put("success", true);
        response.put("message", "Książka usunięta z listy!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/books/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addBook(
            @RequestParam(value = "okladka", required = false) MultipartFile coverImage,
            @RequestParam("link") String link,
            @RequestParam("tytul") String title,
            @RequestParam("autor") String author,
            @RequestParam("opis") String description,
            @RequestParam(value = "gatunki", required = false) String[] genres,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany, aby dodać książkę!");
            return ResponseEntity.status(401).body(response);
        }

        if (title == null || title.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Tytuł książki jest wymagany!");
            return ResponseEntity.badRequest().body(response);
        }

        if (author == null || author.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Autor książki jest wymagany!");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Book book = bookService.addBook(
                    title.trim(), author.trim(), description,
                    coverImage, link, genres, loggedUser
            );

            response.put("success", true);
            response.put("message", "Książka została dodana pomyślnie!");
            response.put("bookId", book.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Wystąpił błąd podczas dodawania książki: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/books/download/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> downloadBook(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Book book = bookService.getBookById(id);

        if (book == null) {
            response.put("success", false);
            response.put("message", "Książka nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        bookService.incrementDownloadCount(id);
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            User freshUser = userService.getFreshUser(loggedUser);
            session.setAttribute("loggedUser", freshUser);
        }

        response.put("success", true);
        response.put("link", book.getLink());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/books/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Book book = bookService.getBookById(id);
        if (book == null) {
            response.put("success", false);
            response.put("message", "Książka nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        if (!loggedUser.isAdmin() && !book.getUserId().equals(loggedUser.getId())) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnień do usunięcia tej książki!");
            return ResponseEntity.status(403).body(response);
        }

        List<BookLike> bookLikes = bookLikeRepository.findByBookId(id);
        bookLikeRepository.deleteAll(bookLikes);

        List<UserBookList> userLists = userBookListRepository.findByBookId(id);
        userBookListRepository.deleteAll(userLists);

        List<UserBookHistory> histories = userBookHistoryRepository.findByBookId(id);
        userBookHistoryRepository.deleteAll(histories);

        boolean deleted = bookService.deleteBook(id, loggedUser);
        if (deleted) {
            response.put("success", true);
            response.put("message", "Książka została usunięta!");
        } else {
            response.put("success", false);
            response.put("message", "Nie udało się usunąć książki!");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/books/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeBook(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Optional<BookLike> existingLike = bookLikeRepository.findByBookIdAndUserId(id, loggedUser.getId());

        if (existingLike.isPresent()) {
            bookLikeRepository.delete(existingLike.get());
            response.put("success", true);
            response.put("liked", false);
        } else {
            BookLike like = new BookLike();
            like.setBookId(id);
            like.setUserId(loggedUser.getId());
            bookLikeRepository.save(like);
            response.put("success", true);
            response.put("liked", true);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/books/{id}/comments")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long id, @RequestBody Map<String, String> body, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "Komentarz nie może być pusty!");
            return ResponseEntity.badRequest().body(response);
        }

        Comment comment = new Comment();
        comment.setContent(content.trim());
        comment.setBookId(id);
        comment.setUserId(loggedUser.getId());
        comment.setUsername(loggedUser.getUsername());
        commentRepository.save(comment);

        response.put("success", true);
        response.put("message", "Komentarz dodany!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/comments/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Komentarz nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        Comment comment = commentOpt.get();
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);

        response.put("success", true);
        response.put("likes", comment.getLikes());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/comments/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Komentarz nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        Comment comment = commentOpt.get();

        if (!loggedUser.isAdmin() && !comment.getUserId().equals(loggedUser.getId())) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnień do usunięcia tego komentarza!");
            return ResponseEntity.status(403).body(response);
        }

        commentRepository.delete(comment);
        response.put("success", true);
        response.put("message", "Komentarz usunięty!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/EdycjaKsiazki/{id}")
    public String editBookForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        Book book = bookService.getBookById(id);

        if (book == null) {
            return "redirect:/Katalog";
        }

        if (loggedUser == null || (!loggedUser.isAdmin() && !book.getUserId().equals(loggedUser.getId()))) {
            return "redirect:/Katalog/" + id;
        }

        model.addAttribute("book", book);
        return "edycja-ksiazki";
    }

    @PostMapping("/api/books/edit/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> editBook(
            @PathVariable Long id,
            @RequestParam(value = "okladka", required = false) MultipartFile coverImage,
            @RequestParam("link") String link,
            @RequestParam("tytul") String title,
            @RequestParam("autor") String author,
            @RequestParam("opis") String description,
            @RequestParam(value = "gatunki", required = false) String[] genres,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");
        Book book = bookService.getBookById(id);

        if (book == null) {
            response.put("success", false);
            response.put("message", "Książka nie istnieje!");
            return ResponseEntity.notFound().build();
        }

        if (loggedUser == null || (!loggedUser.isAdmin() && !book.getUserId().equals(loggedUser.getId()))) {
            response.put("success", false);
            response.put("message", "Nie masz uprawnień do edycji tej książki!");
            return ResponseEntity.status(403).body(response);
        }

        try {
            book.setTitle(title.trim());
            book.setAuthor(author.trim());
            book.setDescription(description);
            book.setLink(link);

            if (genres != null && genres.length > 0) {
                book.setGenres(String.join(",", genres));
            }

            if (coverImage != null && !coverImage.isEmpty()) {
                String contentType = coverImage.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Plik musi być obrazem!");
                }
                if (coverImage.getSize() > 5 * 1024 * 1024) {
                    throw new IllegalArgumentException("Plik nie może przekraczać 5MB!");
                }

                if (book.getCoverImage() != null && book.getCoverImage().startsWith("http")) {
                    try {
                        cloudinaryService.deleteImage(book.getCoverImage());
                    } catch (Exception e) {
                        System.err.println("Nie udało się usunąć starej okładki: " + e.getMessage());
                    }
                }

                String coverImageUrl = cloudinaryService.uploadBookCover(coverImage, loggedUser.getId());
                book.setCoverImage(coverImageUrl);
            }

            bookService.updateBook(book);

            response.put("success", true);
            response.put("message", "Książka została zaktualizowana!");
            response.put("bookId", book.getId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Błąd podczas edycji: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/MojeKsiazki/czytane")
    public String czytaneKsiazki(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);

        List<UserBookList> readingList = userBookListRepository.findByUserIdAndListTypeAndAddedAtAfter(
                loggedUser.getId(), "READING", twoDaysAgo);

        List<Book> readingBooks = new ArrayList<>();
        for (UserBookList item : readingList) {
            Book book = bookService.getBookById(item.getBookId());
            if (book != null) {
                book.setLikesCount(bookLikeRepository.countByBookId(book.getId()));
                book.setCommentsCount(commentRepository.countByBookId(book.getId()));
                readingBooks.add(book);
            }
        }

        model.addAttribute("books", readingBooks);
        model.addAttribute("pageTitle", "Czytane książki");
        model.addAttribute("pageDescription", "Książki które przeglądałeś w ostatnich 2 dniach");
        model.addAttribute("emptyMessage", "Nie masz jeszcze żadnych książek w czytanych. Kliknij 'Przejdź do książki' aby dodać!");
        model.addAttribute("listType", "READING");
        return "kategoria-ksiazek";
    }

    @GetMapping("/MojeKsiazki/ulubione")
    public String ulubioneKsiazki(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        List<BookLike> likes = bookLikeRepository.findByUserId(loggedUser.getId());

        List<Book> favoriteBooks = new ArrayList<>();
        for (BookLike like : likes) {
            Book book = bookService.getBookById(like.getBookId());
            if (book != null) {
                book.setLikesCount(bookLikeRepository.countByBookId(book.getId()));
                book.setCommentsCount(commentRepository.countByBookId(book.getId()));
                favoriteBooks.add(book);
            }
        }

        model.addAttribute("books", favoriteBooks);
        model.addAttribute("pageTitle", "Ulubione książki");
        model.addAttribute("pageDescription", "Książki które polubiłeś");
        model.addAttribute("emptyMessage", "Nie masz jeszcze żadnych polubionych książek. Polub je na stronie książki!");
        model.addAttribute("listType", "FAVORITE");
        return "kategoria-ksiazek";
    }

    @GetMapping("/MojeKsiazki/dodane")
    public String dodaneKsiazki(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            return "redirect:/Logowanie";
        }

        List<Book> userAddedBooks = bookService.getBooksByUser(loggedUser.getId());

        for (Book book : userAddedBooks) {
            book.setLikesCount(bookLikeRepository.countByBookId(book.getId()));
            book.setCommentsCount(commentRepository.countByBookId(book.getId()));
        }

        model.addAttribute("books", userAddedBooks);
        model.addAttribute("pageTitle", "Dodane książki");
        model.addAttribute("pageDescription", "Wszystkie książki które dodałeś do serwisu");
        model.addAttribute("emptyMessage", "Nie masz jeszcze żadnych dodanych książek. Dodaj je przez formularz!");
        model.addAttribute("listType", "MY_BOOKS");
        return "kategoria-ksiazek";
    }

    @PostMapping("/api/books/{id}/reading-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateReadingStatus(
            @PathVariable Long id,
            @RequestParam String status,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        Optional<UserBookList> existing = userBookListRepository.findByUserIdAndBookIdAndListType(
                loggedUser.getId(), id, "READING");

        if (existing.isPresent()) {
            UserBookList userBook = existing.get();
            userBook.setStatus(status);
            userBookListRepository.save(userBook);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/api/user/stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserStats(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        Map<String, Object> response = new HashMap<>();

        if (loggedUser == null) {
            response.put("success", false);
            return ResponseEntity.status(401).body(response);
        }

        User freshUser = userService.getFreshUser(loggedUser);

        int ulubione = bookLikeRepository.countByUserId(loggedUser.getId());

        response.put("success", true);
        response.put("dodaneKsiazki", freshUser.getDodaneKsiazki());
        response.put("ulubione", ulubione);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/books/{id}/reading-history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveReadingHistory(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {
            response.put("success", false);
            response.put("message", "Musisz być zalogowany!");
            return ResponseEntity.status(401).body(response);
        }

        saveBookHistory(loggedUser.getId(), id);

        Optional<UserBookList> reading = userBookListRepository.findByUserIdAndBookIdAndListType(
                loggedUser.getId(), id, "READING");

        if (reading.isPresent()) {
            UserBookList userBook = reading.get();
            userBook.setAddedAt(LocalDateTime.now());
            userBookListRepository.save(userBook);
        } else {
            UserBookList userBook = new UserBookList();
            userBook.setUserId(loggedUser.getId());
            userBook.setBookId(id);
            userBook.setListType("READING");
            userBookListRepository.save(userBook);
        }

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

}