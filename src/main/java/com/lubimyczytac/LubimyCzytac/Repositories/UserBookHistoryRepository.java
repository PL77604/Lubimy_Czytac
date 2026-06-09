package com.lubimyczytac.LubimyCzytac.Repositories;

import com.lubimyczytac.LubimyCzytac.Models.UserBookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserBookHistoryRepository extends JpaRepository<UserBookHistory, Long> {
    List<UserBookHistory> findByUserIdOrderByLastViewedDesc(Long userId);
    Optional<UserBookHistory> findByUserIdAndBookId(Long userId, Long bookId);

    List<UserBookHistory> findByBookId(Long bookId);
}