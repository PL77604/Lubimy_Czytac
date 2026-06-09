package com.lubimyczytac.LubimyCzytac.Repositories;

import com.lubimyczytac.LubimyCzytac.Models.UserBookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBookListRepository extends JpaRepository<UserBookList, Long> {
    List<UserBookList> findByUserIdAndListTypeOrderByAddedAtDesc(Long userId, String listType);
    Optional<UserBookList> findByUserIdAndBookIdAndListType(Long userId, Long bookId, String listType);

    List<UserBookList> findByUserIdAndListTypeAndAddedAtAfter(Long userId, String listType, LocalDateTime date);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserBookList u WHERE u.userId = :userId AND u.bookId = :bookId AND u.listType = :listType")
    void deleteByUserIdAndBookIdAndListType(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("listType") String listType);

    List<UserBookList> findByBookId(Long bookId);
}