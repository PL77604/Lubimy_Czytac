package com.lubimyczytac.LubimyCzytac.Repositories;

import com.lubimyczytac.LubimyCzytac.Models.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookLikeRepository extends JpaRepository<BookLike, Long> {
    Optional<BookLike> findByBookIdAndUserId(Long bookId, Long userId);

    @Query("SELECT COUNT(l) FROM BookLike l WHERE l.bookId = :bookId")
    int countByBookId(Long bookId);
    List<BookLike> findByUserId(Long userId);
    @Query("SELECT COUNT(l) FROM BookLike l WHERE l.userId = :userId")
    int countByUserId(@Param("userId") Long userId);
    List<BookLike> findByBookId(Long bookId);

}