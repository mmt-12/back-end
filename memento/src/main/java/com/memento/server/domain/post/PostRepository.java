package com.memento.server.domain.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<Post> findByIdAndDeletedAtIsNull(Long postId);
}
