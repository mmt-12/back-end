package com.memento.server.domain.comment;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.memento.server.api.service.post.dto.PostCommentDto;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Query("""
        select new com.memento.server.api.service.post.dto.PostCommentDto(
            c.id,
            c.post.id,
            c.associate,
            c.url,
            c.type,
            case when c.type = com.memento.server.domain.comment.CommentType.VOICE then v.temporary else null end
        )
        from Comment c
        left join Voice v 
            on v.url = c.url 
           and c.type = com.memento.server.domain.comment.CommentType.VOICE
        where c.post.id in :postIds
          and c.deletedAt is null
        order by c.post.id, c.createdAt
    """)
	List<PostCommentDto> findPostCommentsByPostIds(
		@Param("postIds") List<Long> postIds,
		@Param("associateId") Long associateId
	);
}
