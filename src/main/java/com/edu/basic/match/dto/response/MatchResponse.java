package com.edu.basic.match.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {

    private Long matchId;

    private Long eventId;

    private Integer score;

    private String status;

    private MatchUser memberA;

    private MatchUser memberB;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchUser {
        private Long userId;
        private String fullName;
        private String gender;
        private Integer age;
        private String profileImageUrl;
    }
}
