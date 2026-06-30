package com.edu.basic.match.service;

import com.edu.basic.match.dto.response.MatchResponse;

import java.util.List;

public interface MatchmakingService {

    /**
     * Builds a weighted compatibility graph from an event's confirmed attendees,
     * runs general-graph maximum-weight matching, persists the result as
     * {@code SUGGESTED} pairings (replacing any previous suggestions), and
     * returns the full set of matches for the event.
     */
    List<MatchResponse> generate(Long eventId);

    List<MatchResponse> getMatches(Long eventId);

    MatchResponse updateStatus(Long matchId, String status);
}
