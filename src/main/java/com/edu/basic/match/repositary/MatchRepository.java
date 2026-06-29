package com.edu.basic.match.repositary;

import com.edu.basic.match.entity.Match;
import com.edu.basic.match.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByEventId(Long eventId);

    void deleteByEventIdAndStatus(Long eventId, MatchStatus status);

    void deleteByEventId(Long eventId);
}
