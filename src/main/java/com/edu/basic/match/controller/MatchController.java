package com.edu.basic.match.controller;

import com.edu.basic.event.dtos.ApiResponse;
import com.edu.basic.match.dto.request.UpdateMatchStatusRequest;
import com.edu.basic.match.dto.response.MatchResponse;
import com.edu.basic.match.service.MatchmakingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Slf4j
@RequiredArgsConstructor
public class MatchController {

    private final MatchmakingService matchmakingService;

    @PostMapping("/events/{eventId}/matchmake")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> generateMatches(@PathVariable Long eventId) {
        log.info("Generate matchmaking request for event {}", eventId);
        List<MatchResponse> matches = matchmakingService.generate(eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Matchmaking completed", matches, HttpStatus.OK.value()));
    }

    @GetMapping("/events/{eventId}/matches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatches(@PathVariable Long eventId) {
        List<MatchResponse> matches = matchmakingService.getMatches(eventId);
        return ResponseEntity.ok(
                ApiResponse.success("Matches retrieved successfully", matches, HttpStatus.OK.value()));
    }

    @PutMapping("/matches/{matchId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MatchResponse>> updateMatchStatus(
            @PathVariable Long matchId,
            @RequestBody UpdateMatchStatusRequest request) {
        MatchResponse match = matchmakingService.updateStatus(matchId, request.getStatus());
        return ResponseEntity.ok(
                ApiResponse.success("Match status updated successfully", match, HttpStatus.OK.value()));
    }
}
