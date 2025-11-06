package com.memento.server.spring.domain.comment;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.comment.CommentFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.memory.MemoryFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.post.PostFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

import jakarta.persistence.EntityManager;

@Transactional
public class CommentRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;
	
	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("id에 해당하는 comment를 조회한다.")
	void findByIdAndDeletedAtIsNull() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = CommentFixtures.comment(fixtures.post, fixtures.associate);
		Comment savedComment = commentRepository.save(comment);

		// when
		Optional<Comment> foundComment = commentRepository.findByIdAndDeletedAtIsNull(savedComment.getId());

		// then
		assertThat(foundComment).isPresent();
		assertThat(foundComment.get()).isEqualTo(savedComment);
	}

	@Test
	@DisplayName("존재하지 않는 id로 조회시 빈 Optional을 반환한다.")
	void findByIdAndDeletedAtIsNullWithNonExistentId() {
		// given
		Long nonExistentId = 999L;

		// when
		Optional<Comment> foundComment = commentRepository.findByIdAndDeletedAtIsNull(nonExistentId);

		// then
		assertThat(foundComment).isNotPresent();
	}

	@Test
	@DisplayName("삭제된 comment는 조회되지 않는다.")
	void findByIdAndDeletedAtIsNullWithDeletedComment() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = CommentFixtures.comment(fixtures.post, fixtures.associate);
		Comment savedComment = commentRepository.save(comment);

		savedComment.markDeleted();
		em.flush();

		// when
		Optional<Comment> foundComment = commentRepository.findByIdAndDeletedAtIsNull(savedComment.getId());

		// then
		assertThat(foundComment).isNotPresent();
	}

	private record Fixtures(
		Member member,
		Community community,
		Associate associate,
		Memory memory,
		Post post
	) {

	}

	private Fixtures createFixtures() {
		Member member = MemberFixtures.member();
		Community community = CommunityFixtures.community(member);
		Associate associate = AssociateFixtures.associate(member, community);
		Memory memory = MemoryFixtures.memory(community, associate);
		Post post = PostFixtures.post(memory, associate);


		Member savedMember = memberRepository.save(member);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate = associateRepository.save(associate);
		Memory savedMemory = memoryRepository.save(memory);
		Post savedPost = postRepository.save(post);

		return new Fixtures(savedMember, savedCommunity, savedAssociate, savedMemory, savedPost);
	}
}
