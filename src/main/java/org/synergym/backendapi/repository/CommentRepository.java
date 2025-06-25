package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
