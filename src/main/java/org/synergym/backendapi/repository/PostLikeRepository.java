package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.PostLike;
import org.synergym.backendapi.entity.PostLikeId;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
}
