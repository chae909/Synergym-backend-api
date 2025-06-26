package org.synergym.backendapi.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Exercise_Likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExerciseLike{

    @EmbeddedId
    private ExerciseLikeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Builder
    public ExerciseLike(User user, Exercise exercise) {
        this.id = new ExerciseLikeId(user.getId(), exercise.getId());
        this.user = user;
        this.exercise = exercise;
    }
}
