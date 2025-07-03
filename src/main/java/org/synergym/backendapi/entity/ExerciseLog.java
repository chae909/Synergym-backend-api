package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "completion_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal completionRate;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

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
}
