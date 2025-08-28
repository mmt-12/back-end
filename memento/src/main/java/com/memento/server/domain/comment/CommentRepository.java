package com.memento.server.domain.comment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);
}
