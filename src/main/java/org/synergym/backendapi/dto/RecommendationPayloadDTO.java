package org.synergym.backendapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // import 추가
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationPayloadDTO {

    @JsonProperty("user_id") // JSON의 "user_id"를 이 필드에 매핑
    private String userId;

    @JsonProperty("user_profile") // JSON의 "user_profile"을 이 필드에 매핑
    private UserDTO userProfile;

    @JsonProperty("posture_analysis")
    private AnalysisHistoryDTO postureAnalysis;

    @JsonProperty("exercise_history")
    private List<ExerciseLogDTO> exerciseHistory;

    @JsonProperty("liked_exercises")
    private List<ExerciseLikeDTO> likedExercises;

    @JsonProperty("user_routines")
    private List<RoutineDTO> userRoutines;
}