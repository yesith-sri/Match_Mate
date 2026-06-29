package com.edu.basic.match.service.impl;

import com.edu.basic.booking.entity.Booking;
import com.edu.basic.booking.enums.BookingStatus;
import com.edu.basic.booking.repositary.BookingRepository;
import com.edu.basic.event.repository.EventRepository;
import com.edu.basic.exception.ErrorCode;
import com.edu.basic.exception.ResourceNotFoundException;
import com.edu.basic.match.dto.response.MatchResponse;
import com.edu.basic.match.entity.Match;
import com.edu.basic.match.enums.MatchStatus;
import com.edu.basic.match.repositary.MatchRepository;
import com.edu.basic.match.service.MatchmakingService;
import com.edu.basic.user.entity.User;
import com.edu.basic.user.repositary.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.MatchingAlgorithm;
import org.jgrapht.alg.matching.blossom.v5.KolmogorovWeightedMatching;
import org.jgrapht.alg.matching.blossom.v5.ObjectiveSense;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.util.SupplierUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchmakingServiceImpl implements MatchmakingService {

    private static final String ANY_GENDER = "ANY";

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public List<MatchResponse> generate(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND,
                    "Event not found with id: " + eventId);
        }

        // 1. Load confirmed attendees -> unique users.
        List<Booking> bookings =
                bookingRepository.findByEvent_EventIdAndStatus(eventId, BookingStatus.CONFIRMED);

        Map<Long, User> usersById = new LinkedHashMap<>();
        for (Booking booking : bookings) {
            User user = booking.getUser();
            if (user != null && user.getId() != null) {
                usersById.putIfAbsent(user.getId(), user);
            }
        }
        List<User> users = new ArrayList<>(usersById.values());

        // 2. Build a weighted compatibility graph: an edge only exists between a
        //    mutually-compatible pair, weighted by their compatibility score.
        //    A vertex supplier (negative ids) is required because the Blossom V
        //    max-weight matching reduction may add auxiliary vertices internally;
        //    negatives can never collide with real (positive) user ids.
        AtomicLong auxVertexId = new AtomicLong(-1L);
        Graph<Long, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(
                auxVertexId::getAndDecrement,
                SupplierUtil.createDefaultWeightedEdgeSupplier());
        for (User user : users) {
            graph.addVertex(user.getId());
        }
        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                User a = users.get(i);
                User b = users.get(j);
                if (compatible(a, b)) {
                    int weight = score(a, b);
                    if (weight > 0) {
                        DefaultWeightedEdge edge = graph.addEdge(a.getId(), b.getId());
                        if (edge != null) {
                            graph.setEdgeWeight(edge, weight);
                        }
                    }
                }
            }
        }

        // 3. Run general-graph maximum-weight matching (Blossom V). Vertices that
        //    cannot be paired profitably are simply left unmatched.
        List<Match> newMatches = new ArrayList<>();
        if (!graph.edgeSet().isEmpty()) {
            MatchingAlgorithm<Long, DefaultWeightedEdge> algorithm =
                    new KolmogorovWeightedMatching<>(graph, ObjectiveSense.MAXIMIZE);
            MatchingAlgorithm.Matching<Long, DefaultWeightedEdge> matching = algorithm.getMatching();

            for (DefaultWeightedEdge edge : matching.getEdges()) {
                Long userAId = graph.getEdgeSource(edge);
                Long userBId = graph.getEdgeTarget(edge);
                // Skip any auxiliary (dummy) vertices added by the reduction.
                if (!usersById.containsKey(userAId) || !usersById.containsKey(userBId)) {
                    continue;
                }
                int weight = (int) Math.round(graph.getEdgeWeight(edge));
                newMatches.add(Match.builder()
                        .eventId(eventId)
                        .userAId(userAId)
                        .userBId(userBId)
                        .score(weight)
                        .status(MatchStatus.SUGGESTED)
                        .build());
            }
        }

        // 4. Replace the entire previous result set: every re-run wipes all prior
        //    matches for this event (suggested/confirmed/rejected) and saves a fresh
        //    set, so repeated clicks never accumulate duplicates.
        matchRepository.deleteByEventId(eventId);
        matchRepository.flush();
        matchRepository.saveAll(newMatches);

        log.info("Generated {} suggested matches for event {}", newMatches.size(), eventId);
        return getMatches(eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatches(Long eventId) {
        return matchRepository.findByEventId(eventId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MatchResponse updateStatus(Long matchId, String status) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Match not found with id: " + matchId));

        MatchStatus newStatus;
        try {
            newStatus = MatchStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ResourceNotFoundException(ErrorCode.INVALID_REQUEST,
                    "Invalid match status: " + status);
        }

        match.setStatus(newStatus);
        return toResponse(matchRepository.save(match));
    }

    // ── Compatibility & scoring ────────────────────────────────────────────

    private boolean compatible(User a, User b) {
        return genderPreferenceOk(a, b)
                && genderPreferenceOk(b, a)
                && agePreferenceOk(a, b)
                && agePreferenceOk(b, a);
    }

    private boolean genderPreferenceOk(User viewer, User candidate) {
        String seeking = viewer.getSeekingGender();
        if (seeking == null || seeking.isBlank() || ANY_GENDER.equalsIgnoreCase(seeking)) {
            return true;
        }
        return seeking.equalsIgnoreCase(candidate.getGender());
    }

    private boolean agePreferenceOk(User viewer, User candidate) {
        Integer candidateAge = candidate.getAge();
        if (candidateAge == null) {
            return true;
        }
        if (viewer.getMinAgePref() != null && candidateAge < viewer.getMinAgePref()) {
            return false;
        }
        if (viewer.getMaxAgePref() != null && candidateAge > viewer.getMaxAgePref()) {
            return false;
        }
        return true;
    }

    private int score(User a, User b) {
        int s = 0;

        // Age closeness: closer ages score higher.
        if (a.getAge() != null && b.getAge() != null) {
            s += Math.max(0, 30 - Math.abs(a.getAge() - b.getAge()) * 3);
        }

        // Location proximity.
        if (eq(a.getCity(), b.getCity())) {
            s += 20;
        } else if (eq(a.getCountry(), b.getCountry())) {
            s += 10;
        }

        // Shared interests.
        long shared = intersectionSize(a.getInterests(), b.getInterests());
        s += (int) Math.min(50, shared * 10);

        // Keep a positive floor so any compatible pair is eligible for matching.
        return Math.max(1, Math.min(100, s));
    }

    private boolean eq(String x, String y) {
        return x != null && !x.isBlank() && x.equalsIgnoreCase(y);
    }

    private long intersectionSize(Set<String> x, Set<String> y) {
        if (x == null || y == null || x.isEmpty() || y.isEmpty()) {
            return 0;
        }
        Set<String> normalizedY = y.stream()
                .filter(v -> v != null)
                .map(v -> v.trim().toLowerCase())
                .collect(Collectors.toSet());
        return x.stream()
                .filter(v -> v != null)
                .map(v -> v.trim().toLowerCase())
                .filter(normalizedY::contains)
                .count();
    }

    private MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .matchId(match.getMatchId())
                .eventId(match.getEventId())
                .score(match.getScore())
                .status(match.getStatus() != null ? match.getStatus().name() : null)
                .memberA(toUserDto(match.getUserAId()))
                .memberB(toUserDto(match.getUserBId()))
                .build();
    }

    private MatchResponse.MatchUser toUserDto(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .map(user -> MatchResponse.MatchUser.builder()
                        .userId(user.getId())
                        .fullName(fullName(user))
                        .gender(user.getGender())
                        .age(user.getAge())
                        .profileImageUrl(user.getProfileImageUrl())
                        .build())
                .orElse(null);
    }

    private String fullName(User user) {
        String first = user.getFirstName() != null ? user.getFirstName() : "";
        String last = user.getLastName() != null ? user.getLastName() : "";
        return (first + " " + last).trim();
    }
}
