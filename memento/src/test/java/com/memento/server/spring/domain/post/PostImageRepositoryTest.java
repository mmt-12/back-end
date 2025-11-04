package com.memento.server.spring.domain.post;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.Location;
import com.memento.server.domain.memory.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Hash;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

class PostImageRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private PostImageRepository postImageRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Test
	@DisplayName("메모리 ID로 모든 포스트 이미지를 조회한다.")
	void findAllByMemoryId_메모리_ID로_모든_포스트_이미지를_조회한다() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("추억")
			.description("설명")
			.location(Location.builder()
				.address("주소")
				.name("장소")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(2))
				.endTime(LocalDateTime.now().minusDays(1))
				.build())
			.community(community)
			.associate(associate)
			.build());

		Post post1 = postRepository.save(Post.builder().content("포스트1").memory(memory).associate(associate).build());
		Post post2 = postRepository.save(Post.builder().content("포스트2").memory(memory).associate(associate).build());

		PostImage postImage1 = postImageRepository.save(PostImage.builder().url("image1.jpg").hash(Hash.builder().hash("hash1").build()).post(post1).build());
		PostImage postImage2 = postImageRepository.save(PostImage.builder().url("image2.png").hash(Hash.builder().hash("hash2").build()).post(post1).build());
		PostImage postImage3 = postImageRepository.save(PostImage.builder().url("image3.gif").hash(Hash.builder().hash("hash3").build()).post(post2).build());

		// when
		List<PostImage> foundImages = postImageRepository.findAllByMemoryId(memory.getId());

		// then
		assertThat(foundImages).hasSize(3);
		assertThat(foundImages).extracting(PostImage::getUrl)
			.containsExactlyInAnyOrder("image1.jpg", "image2.png", "image3.gif");
	}

	@Test
	@DisplayName("삭제된 포스트 이미지는 메모리 ID로 조회되지 않는다.")
	void findAllByMemoryId_삭제된_이미지는_조회되지_않는다() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("추억")
			.description("설명")
			.location(Location.builder()
				.address("주소")
				.name("장소")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(2))
				.endTime(LocalDateTime.now().minusDays(1))
				.build())
			.community(community)
			.associate(associate)
			.build());

		Post post = postRepository.save(Post.builder().content("포스트").memory(memory).associate(associate).build());

		PostImage postImage1 = postImageRepository.save(PostImage.builder().url("image1.jpg").hash(Hash.builder().hash("hash1").build()).post(post).build());
		PostImage postImage2 = postImageRepository.save(PostImage.builder().url("image2.png").hash(Hash.builder().hash("hash2").build()).post(post).build());
		postImageRepository.delete(postImage2);

		// when
		List<PostImage> foundImages = postImageRepository.findAllByMemoryId(memory.getId());

		// then
		assertThat(foundImages).hasSize(1);
		assertThat(foundImages).extracting(PostImage::getUrl).containsExactly("image1.jpg");
	}

	@Test
	@DisplayName("메모리 ID에 해당하는 이미지가 없으면 빈 리스트를 반환한다.")
	void findAllByMemoryId_이미지_없음() {
		// given
		Long nonExistentMemoryId = 9999L;

		// when
		List<PostImage> foundImages = postImageRepository.findAllByMemoryId(nonExistentMemoryId);

		// then
		assertThat(foundImages).isEmpty();
	}
}
