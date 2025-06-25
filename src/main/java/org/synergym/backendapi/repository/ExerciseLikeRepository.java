package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.ExerciseLike;
import org.synergym.backendapi.entity.ExerciseLikeId;

public interface ExerciseLikeRepository extends JpaRepository<ExerciseLike, ExerciseLikeId> {
}