package org.synergym.backendapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisHistoryDTO {

    private int id;
    private int userId;
    private int spineCurvScore;
    private int spineScolScore;
    private int pelvicScore;
    private int neckScore;
    private int shoulderScore;
    private String frontImageUrl;
    private String sideImageUrl;
    private LocalDateTime createdAt;
}
