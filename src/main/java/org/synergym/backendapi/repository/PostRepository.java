package org.synergym.backendapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.synergym.backendapi.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> { // ID 타입은 int
}
