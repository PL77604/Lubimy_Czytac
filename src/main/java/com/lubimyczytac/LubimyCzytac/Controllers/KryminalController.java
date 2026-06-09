package com.lubimyczytac.LubimyCzytac.Controllers;

import com.lubimyczytac.LubimyCzytac.Models.Book;
import com.lubimyczytac.LubimyCzytac.Repositories.BookLikeRepository;
import com.lubimyczytac.LubimyCzytac.Repositories.CommentRepository;
import com.lubimyczytac.LubimyCzytac.Services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class KryminalController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookLikeRepository bookLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/Kryminal")
    public String kryminal(Model model) {
        List<Book> allBooks = bookService.getAllBooks();

        List<Book> kryminalBooks = allBooks.stream()
                .filter(book -> book.getGenres() != null &&
                        (book.getGenres().toLowerCase().contains("kryminal") ||
                                book.getGenres().toLowerCase().contains("kryminał")))
                .collect(Collectors.toList());

        for (Book book : kryminalBooks) {
            int likesCount = bookLikeRepository.countByBookId(book.getId());
            int commentsCount = commentRepository.countByBookId(book.getId());
            book.setLikesCount(likesCount);
            book.setCommentsCount(commentsCount);
        }

        model.addAttribute("books", kryminalBooks);
        return "kryminal";
    }
}