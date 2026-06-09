package com.lubimyczytac.LubimyCzytac.Repositories;

import com.lubimyczytac.LubimyCzytac.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByUserId(Long userId);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Book> searchByTitle(@Param("search") String search);

    @Query("SELECT b FROM Book b WHERE b.genres LIKE CONCAT('%', :genre, '%')")
    List<Book> findByGenre(@Param("genre") String genre);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) AND b.genres LIKE CONCAT('%', :genre, '%')")
    List<Book> searchByTitleAndGenre(@Param("search") String search, @Param("genre") String genre);
}