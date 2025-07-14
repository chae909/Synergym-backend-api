package org.synergym.backendapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Emotion_Logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emotion_log_id")
    private int id;

    // exercise_log_id를 외래 키로
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id", nullable = false, unique = true)
    private ExerciseLog exerciseLog;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmotionType emotion;

    @Builder
    public EmotionLog(ExerciseLog exerciseLog, EmotionType emotion) {
        this.exerciseLog = exerciseLog;
        this.emotion = emotion;
    }

    public void updateEmotion(EmotionType emotion) {
        this.emotion = emotion;
    }
}