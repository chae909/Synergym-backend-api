package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Analysis_History")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "spine_curv_score", nullable = false)
    private int spineCurvScore;

    @Column(name = "spine_scol_score", nullable = false)
    private int spineScolScore;

    @Column(name = "pelvic_score", nullable = false)
    private int pelvicScore;

    @Column(name = "neck_score", nullable = false)
    private int neckScore;

    @Column(name = "shoulder_score", nullable = false)
    private int shoulderScore;

    @Column(name = "front_image_url", length = 255)
    private String frontImageUrl;

    @Column(name = "side_image_url", length = 255)
    private String sideImageUrl;

    @Builder
    public AnalysisHistory(User user, int spineCurvScore, int spineScolScore, int pelvicScore, int neckScore, int shoulderScore, String frontImageUrl, String sideImageUrl) {
        this.user = user;
        this.spineCurvScore = spineCurvScore;
        this.spineScolScore = spineScolScore;
        this.pelvicScore = pelvicScore;
        this.neckScore = neckScore;
        this.shoulderScore = shoulderScore;
        this.frontImageUrl = frontImageUrl;
        this.sideImageUrl = sideImageUrl;
    }
}
