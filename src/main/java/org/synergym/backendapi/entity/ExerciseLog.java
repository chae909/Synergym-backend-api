package org.synergym.backendapi.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Exercise_Logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exercise_date", nullable = false)
    private LocalDate exerciseDate;

    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    // EmotionLog와의 양방향 관계 설정
    @OneToOne(mappedBy = "exerciseLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmotionLog emotionLog;

    @Builder
    public ExerciseLog(User user, LocalDate exerciseDate, BigDecimal completionRate, String memo) {
        this.user = user;
        this.exerciseDate = exerciseDate;
        this.completionRate = completionRate;
        this.memo = memo;
    }

    public void updateUser(User newUser) {
        this.user = newUser;
    }

    public void updateExerciseDate(LocalDate newExerciseDate) {
        this.exerciseDate = newExerciseDate;
    }

    public void updateCompletionRate(BigDecimal newCompletionRate) {
        this.completionRate = newCompletionRate;
    }

    public void updateMemo(String newMemo) {
        this.memo = newMemo;
    }
    
    // 연관관계 편의 메소드
    public void setEmotionLog(EmotionLog emotionLog) {
        this.emotionLog = emotionLog;
    }
}