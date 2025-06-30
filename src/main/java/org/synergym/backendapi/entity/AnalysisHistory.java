package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Analysis_History")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("use_yn = 'Y'")
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

    public void updateSpineCurvScore(int newSpineCurvScore) {
        this.spineCurvScore = newSpineCurvScore;
    }

    public void updateSpineScolScore(int newSpineScolScore) {
        this.spineScolScore = newSpineScolScore;
    }

    public void updatePelvicScore(int newPelvicScore) {
        this.pelvicScore = newPelvicScore;
    }

    public void updateNeckScore(int newNeckScore) {
        this.neckScore = newNeckScore;
    }

    public void updateShoulderScore(int newShoulderScore) {
        this.shoulderScore = newShoulderScore;
    }

    public void updateFrontImageUrl(String newFrontImageUrl) {
        this.frontImageUrl = newFrontImageUrl;
    }

    public void updateSideImageUrl(String newSideImageUrl) {
        this.sideImageUrl = newSideImageUrl;
    }
}
