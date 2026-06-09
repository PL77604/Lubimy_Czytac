// CommentRepository.java
package com.lubimyczytac.LubimyCzytac.Repositories;

import com.lubimyczytac.LubimyCzytac.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBookIdOrderByCreatedAtDesc(Long bookId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.bookId = :bookId")
    int countByBookId(@Param("bookId") Long bookId);
}
