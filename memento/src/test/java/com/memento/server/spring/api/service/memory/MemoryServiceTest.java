package com.memento.server.spring.api.service.memory;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_EXISTENCE;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_AUTHOR;
import static com.memento.server.common.error.ErrorCodes.MEMORY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import com.memento.server.api.controller.memory.dto.request.CreateUpdateMemoryRequest;
import com.memento.server.api.controller.memory.dto.request.ReadMemoryListRequest;
import com.memento.server.api.controller.memory.dto.response.CreateUpdateMemoryResponse;
import com.memento.server.api.controller.memory.dto.response.DownloadImagesResponse;
import com.memento.server.api.controller.memory.dto.response.ReadMemoryListResponse;
import com.memento.server.api.controller.memory.dto.response.ReadMemoryResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.memory.MemoryService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.Location;
import com.memento.server.domain.memory.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.MemoryAssociate;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Hash;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;

@SpringBootTest
class MemoryServiceTest {

	@Autowired
	private MemoryService memoryService;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private MemoryAssociateRepository memoryAssociateRepository;

	@Autowired
	private PostImageRepository postImageRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@AfterEach
	void afterEach() {
		memoryRepository.deleteAllInBatch();
		memoryAssociateRepository.deleteAllInBatch();
		postImageRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("단일 기억을 조회한다.")
	void read_success() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory = memoryRepository.save(
			Memory.builder()
				.title("단일 기억")
				.description("단일 기억에 대한 설명")
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
		postImageRepository.save(PostImage.builder().url("image1.jpg").hash(Hash.builder().hash("hash1").build()).post(post).build());
		postImageRepository.save(PostImage.builder().url("image2.jpg").hash(Hash.builder().hash("hash2").build()).post(post).build());

		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build()); // Duplicate for count

		// when
		ReadMemoryResponse response = memoryService.read(memory.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(memory.getId());
		assertThat(response.title()).isEqualTo("단일 기억");
		assertThat(response.description()).isEqualTo("단일 기억에 대한 설명");
		assertThat(response.pictures()).hasSize(2);
		assertThat(response.memberAmount()).isEqualTo(2L);
	}

	@Test
	@DisplayName("존재하지 않는 기억을 단일 조회하면 예외가 발생한다.")
	void read_memoryNotFound() {
		// given
		Long nonExistentMemoryId = 9999L;

		// when // then
		assertThatThrownBy(() -> memoryService.read(nonExistentMemoryId))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_FOUND);
			});
	}


	@Test
	@DisplayName("모든 기억을 조회한다.")
	void readAll() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory1 = memoryRepository.save(
			Memory.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("주소1")
					.name("장소1")
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
		Memory memory2 = memoryRepository.save(Memory.builder()
			.title("추억2")
			.description("내용2")
			.location(Location.builder()
				.address("주소2")
				.name("장소2")
				.latitude(BigDecimal.valueOf(2.0))
				.longitude(BigDecimal.valueOf(2.0))
				.code(2)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(4))
				.endTime(LocalDateTime.now().minusDays(3))
				.build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory3 = memoryRepository.save(Memory.builder()
			.title("추억3")
			.description("내용3")
			.location(Location.builder()
				.address("주소3")
				.name("장소3")
				.latitude(BigDecimal.valueOf(3.0))
				.longitude(BigDecimal.valueOf(3.0))
				.code(3)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(6))
				.endTime(LocalDateTime.now().minusDays(5))
				.build())
			.community(community)
			.associate(associate)
			.build());

		Post post1 = postRepository.save(Post.builder().content("포스트1").memory(memory1).associate(associate).build());
		Post post2 = postRepository.save(Post.builder().content("포스트2").memory(memory2).associate(associate).build());
		Post post3 = postRepository.save(Post.builder().content("포스트3").memory(memory3).associate(associate).build());

		postImageRepository.save(
			PostImage.builder().url("image1.jpg").hash(Hash.builder().hash("hash1").build()).post(post1).build());
		postImageRepository.save(
			PostImage.builder().url("image2.jpg").hash(Hash.builder().hash("hash2").build()).post(post1).build());
		postImageRepository.save(
			PostImage.builder().url("image3.jpg").hash(Hash.builder().hash("hash3").build()).post(post2).build());

		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory1).associate(associate).build());
		memoryAssociateRepository.save(
			MemoryAssociate.builder().memory(memory1).associate(associate).build()); // Duplicate to test count
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory2).associate(associate).build());

		// when
		ReadMemoryListRequest request = ReadMemoryListRequest.builder()
			.cursor(null)
			.size(10)
			.keyword(null)
			.startTime(null)
			.endTime(null)
			.build();
		ReadMemoryListResponse response = memoryService.readAll(community.getId(), request);

		// then
		assertThat(response.memories()).hasSize(3);
		assertThat(response.memories().get(0).title()).isEqualTo("추억3");
		assertThat(response.memories().get(0).pictures()).isEmpty();
		assertThat(response.memories().get(0).memberAmount()).isEqualTo(0);

		assertThat(response.memories().get(1).title()).isEqualTo("추억2");
		assertThat(response.memories().get(1).pictures()).hasSize(1);
		assertThat(response.memories().get(1).memberAmount()).isEqualTo(1);

		assertThat(response.memories().get(2).title()).isEqualTo("추억1");
		assertThat(response.memories().get(2).pictures()).hasSize(2);
		assertThat(response.memories().get(2).memberAmount()).isEqualTo(2);

		assertThat(response.hasNext()).isFalse();
		assertThat(response.nextCursor()).isEqualTo(memory1.getId());
	}

	@Test
	@DisplayName("커서 기반으로 기억을 조회한다.")
	void readAll_withCursor() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory1 = memoryRepository.save(
			Memory.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("a")
					.name("n")
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
		Memory memory2 = memoryRepository.save(Memory.builder()
			.title("추억2")
			.description("내용2")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(2)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(4))
				.endTime(LocalDateTime.now().minusDays(3))
				.build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory3 = memoryRepository.save(Memory.builder()
			.title("추억3")
			.description("내용3")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(3)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now().minusDays(6))
				.endTime(LocalDateTime.now().minusDays(5))
				.build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory4 = memoryRepository.save(Memory.builder()
			.title("추억4")
			.description("내용4")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(4)
				.build())
			.period(Period.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory5 = memoryRepository.save(Memory.builder()
			.title("추억5")
			.description("내용5")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(5)
				.build())
			.period(Period.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).build())
			.community(community)
			.associate(associate)
			.build());

		// when
		ReadMemoryListRequest request1 = ReadMemoryListRequest.builder()
			.cursor(null)
			.size(2)
			.keyword(null)
			.startTime(null)
			.endTime(null)
			.build();
		ReadMemoryListResponse response1 = memoryService.readAll(community.getId(), request1);

		// then
		assertThat(response1.memories()).hasSize(2);
		assertThat(response1.memories().get(0).title()).isEqualTo("추억5");
		assertThat(response1.memories().get(1).title()).isEqualTo("추억4");
		assertThat(response1.hasNext()).isTrue();
		assertThat(response1.nextCursor()).isEqualTo(memory4.getId());

		// when
		ReadMemoryListRequest request2 = ReadMemoryListRequest.builder()
			.cursor(response1.nextCursor())
			.size(2)
			.keyword(null)
			.startTime(null)
			.endTime(null)
			.build();
		ReadMemoryListResponse response2 = memoryService.readAll(community.getId(), request2);

		// then
		assertThat(response2.memories()).hasSize(2);
		assertThat(response2.memories().get(0).title()).isEqualTo("추억3");
		assertThat(response2.memories().get(1).title()).isEqualTo("추억2");
		assertThat(response2.hasNext()).isTrue();
		assertThat(response2.nextCursor()).isEqualTo(memory2.getId());

		// when
		ReadMemoryListRequest request3 = ReadMemoryListRequest.builder()
			.cursor(response2.nextCursor())
			.size(2)
			.keyword(null)
			.startTime(null)
			.endTime(null)
			.build();
		ReadMemoryListResponse response3 = memoryService.readAll(community.getId(), request3);

		// then
		assertThat(response3.memories()).hasSize(1);
		assertThat(response3.memories().get(0).title()).isEqualTo("추억1");
		assertThat(response3.hasNext()).isFalse();
		assertThat(response3.nextCursor()).isEqualTo(memory1.getId());
	}

	@Test
	@DisplayName("키워드로 기억을 조회한다.")
	void readAll_withKeyword() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory1 = memoryRepository.save(
			Memory.builder()
				.title("여행 추억")
				.description("제주도 여행")
				.location(Location.builder()
					.address("a")
					.name("n")
					.latitude(BigDecimal.valueOf(1.0))
					.longitude(BigDecimal.valueOf(1.0))
					.code(1)
					.build())
				.period(Period.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).build())
				.community(community)
				.associate(associate)
				.build());
		Memory memory2 = memoryRepository.save(Memory.builder()
			.title("친구들과의 추억")
			.description("강릉 바다")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(2)
				.build())
			.period(Period.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory3 = memoryRepository.save(Memory.builder()
			.title("가족 여행")
			.description("부산 해운대")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(3)
				.build())
			.period(Period.builder().startTime(LocalDateTime.now()).endTime(LocalDateTime.now()).build())
			.community(community)
			.associate(associate)
			.build());

		// when
		ReadMemoryListRequest request = ReadMemoryListRequest.builder()
			.cursor(null)
			.size(10)
			.keyword("여행")
			.startTime(null)
			.endTime(null)
			.build();
		ReadMemoryListResponse response = memoryService.readAll(community.getId(), request);

		// then
		assertThat(response.memories()).hasSize(2);
		assertThat(response.memories().get(0).title()).isEqualTo("가족 여행");
		assertThat(response.memories().get(1).title()).isEqualTo("여행 추억");
	}

	@Test
	@DisplayName("기간으로 기억을 조회한다.")
	void readAll_withDateRange() throws NoSuchFieldException, IllegalAccessException {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory1 = memoryRepository.save(
			Memory.builder()
				.title("추억1")
				.description("내용1")
				.location(Location.builder()
					.address("a")
					.name("n")
					.latitude(BigDecimal.valueOf(1.0))
					.longitude(BigDecimal.valueOf(1.0))
					.code(1)
					.build())
				.period(Period.builder()
					.startTime(LocalDate.now().atStartOfDay())
					.endTime(LocalDate.now().atStartOfDay().plusHours(1))
					.build())
				.community(community)
				.associate(associate)
				.build());
		Memory memory2 = memoryRepository.save(Memory.builder()
			.title("추억2")
			.description("내용2")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(2)
				.build())
			.period(Period.builder()
				.startTime(LocalDate.now().atStartOfDay())
				.endTime(LocalDate.now().atStartOfDay().plusHours(1))
				.build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory3 = memoryRepository.save(Memory.builder()
			.title("추억3")
			.description("내용3")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(3)
				.build())
			.period(Period.builder()
				.startTime(LocalDate.now().minusDays(1).atStartOfDay())
				.endTime(LocalDate.now().minusDays(1).atStartOfDay().plusHours(1))
				.build())
			.community(community)
			.associate(associate)
			.build());
		Memory memory4 = memoryRepository.save(Memory.builder()
			.title("추억4")
			.description("내용4")
			.location(Location.builder()
				.address("a")
				.name("n")
				.latitude(BigDecimal.valueOf(1.0))
				.longitude(BigDecimal.valueOf(1.0))
				.code(4)
				.build())
			.period(Period.builder()
				.startTime(LocalDate.now().minusDays(2).atStartOfDay())
				.endTime(LocalDate.now().minusDays(2).atStartOfDay().plusHours(1))
				.build())
			.community(community)
			.associate(associate)
			.build());

		// when
		LocalDate startDate = LocalDate.now().minusDays(1);
		LocalDate endDate = LocalDate.now();
		ReadMemoryListRequest request = ReadMemoryListRequest.builder()
			.cursor(null)
			.size(10)
			.keyword(null)
			.startTime(startDate)
			.endTime(endDate)
			.build();
		ReadMemoryListResponse response = memoryService.readAll(community.getId(), request);

		// then
		assertThat(response.memories()).hasSize(3);
		assertThat(response.memories().get(0).title()).isEqualTo("추억3");
		assertThat(response.memories().get(1).title()).isEqualTo("추억2");
		assertThat(response.memories().get(2).title()).isEqualTo("추억1");
	}

	@Test
	@DisplayName("기억을 생성한다.")
	void createMemory_success() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
		Associate otherAssociate = associateRepository.save(Associate.create("다른어소시에이트", member, community));

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of(associate.getId(), otherAssociate.getId()))
			.build();

		// when
		CreateUpdateMemoryResponse response = memoryService.create(community.getId(), associate.getId(), request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.memoryId()).isNotNull();

		transactionTemplate.execute(status -> {
			Memory foundMemory = memoryRepository.findByIdAndDeletedAtIsNull(response.memoryId()).orElseThrow();
			assertThat(foundMemory.getTitle()).isEqualTo("새로운 추억");
			assertThat(foundMemory.getDescription()).isEqualTo("새로운 추억에 대한 설명입니다.");
			assertThat(foundMemory.getLocation().getAddress()).isEqualTo("테스트 주소");
			assertThat(foundMemory.getPeriod().getStartTime()).isEqualTo(
				LocalDateTime.of(2024, 8, 1, 10, 0));

			List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(
				foundMemory);
			assertThat(memoryAssociates).hasSize(2);
			assertThat(memoryAssociates).extracting(MemoryAssociate::getAssociate)
				.containsExactlyInAnyOrder(associate, otherAssociate);

			return null;
		});
	}

	@Test
	@DisplayName("존재하지 않는 참여자로 기억을 생성하면 예외가 발생한다.")
	void createMemory_associateNotFound() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1005L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of())
			.build();

		Long nonExistentAssociateId = 9999L;

		// when // then
		assertThatThrownBy(() -> memoryService.create(community.getId(), nonExistentAssociateId, request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(ASSOCIATE_NOT_EXISTENCE);
			});
	}

	@Test
	@DisplayName("존재하지 않는 커뮤니티로 기억을 생성하면 예외가 발생한다.")
	void createMemory_communityNotFound() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1006L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of())
			.build();

		Long nonExistentCommunityId = 9999L;

		// when // then
		assertThatThrownBy(() -> memoryService.create(nonExistentCommunityId, associate.getId(), request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(COMMUNITY_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("기억을 수정한다. - 참여자 추가")
	void updateMemory_success_associate_add() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
		Associate otherAssociate = associateRepository.save(Associate.create("다른어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("기존 추억")
			.description("기존 추억에 대한 설명입니다.")
			.location(Location.builder()
				.address("기존 주소")
				.name("기존 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(associate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build());

		CreateUpdateMemoryRequest.LocationRequest updatedLocationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("수정된 주소")
			.name("수정된 장소")
			.latitude(30.0F)
			.longitude(40.0F)
			.code(2)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest updatedPeriodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 9, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 9, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("수정된 추억")
			.description("수정된 추억에 대한 설명입니다.")
			.location(updatedLocationRequest)
			.period(updatedPeriodRequest)
			.associates(List.of(associate.getId(), otherAssociate.getId()))
			.build();

		// when
		CreateUpdateMemoryResponse response = memoryService.update(request, associate.getId(), memory.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.memoryId()).isEqualTo(memory.getId());

		transactionTemplate.execute(status -> {
			Memory foundMemory = memoryRepository.findByIdAndDeletedAtIsNull(response.memoryId()).orElseThrow();
			assertThat(foundMemory.getTitle()).isEqualTo("수정된 추억");
			assertThat(foundMemory.getDescription()).isEqualTo("수정된 추억에 대한 설명입니다.");
			assertThat(foundMemory.getLocation().getAddress()).isEqualTo("수정된 주소");
			assertThat(foundMemory.getPeriod().getStartTime()).isEqualTo(LocalDateTime.of(2024, 9, 1, 10, 0));

			List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(
				foundMemory);
			assertThat(memoryAssociates).hasSize(2);
			assertThat(memoryAssociates).extracting(MemoryAssociate::getAssociate)
				.containsExactlyInAnyOrder(associate, otherAssociate);

			return null;
		});
	}

	@Test
	@DisplayName("기억을 수정한다. - 참여자 감소")
	void updateMemory_success_associate_remove() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
		Associate otherAssociate = associateRepository.save(Associate.create("다른어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("기존 추억")
			.description("기존 추억에 대한 설명입니다.")
			.location(Location.builder()
				.address("기존 주소")
				.name("기존 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(associate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build());

		CreateUpdateMemoryRequest.LocationRequest updatedLocationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("수정된 주소")
			.name("수정된 장소")
			.latitude(30.0F)
			.longitude(40.0F)
			.code(2)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest updatedPeriodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 9, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 9, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("수정된 추억")
			.description("수정된 추억에 대한 설명입니다.")
			.location(updatedLocationRequest)
			.period(updatedPeriodRequest)
			.associates(List.of(otherAssociate.getId()))
			.build();

		// when
		CreateUpdateMemoryResponse response = memoryService.update(request, associate.getId(), memory.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.memoryId()).isEqualTo(memory.getId());

		transactionTemplate.execute(status -> {
			Memory foundMemory = memoryRepository.findByIdAndDeletedAtIsNull(response.memoryId()).orElseThrow();
			assertThat(foundMemory.getTitle()).isEqualTo("수정된 추억");
			assertThat(foundMemory.getDescription()).isEqualTo("수정된 추억에 대한 설명입니다.");
			assertThat(foundMemory.getLocation().getAddress()).isEqualTo("수정된 주소");
			assertThat(foundMemory.getPeriod().getStartTime()).isEqualTo(LocalDateTime.of(2024, 9, 1, 10, 0));

			List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(
				foundMemory);
			assertThat(memoryAssociates).hasSize(1);
			assertThat(memoryAssociates).extracting(MemoryAssociate::getAssociate)
				.containsExactlyInAnyOrder(otherAssociate);

			return null;
		});
	}

	@Test
	@DisplayName("존재하지 않는 기억을 수정하면 예외가 발생한다.")
	void updateMemory_memoryNotFound() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		CreateUpdateMemoryRequest.LocationRequest locationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest periodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of())
			.build();

		Long nonExistentMemoryId = 9999L;

		// when // then
		assertThatThrownBy(() -> memoryService.update(request, associate.getId(), nonExistentMemoryId))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("기억의 작성자가 아닌 경우 기억을 수정하면 예외가 발생한다.")
	void updateMemory_notAuthor() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1009L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate authorAssociate = associateRepository.save(Associate.create("작성자어소시에이트", member, community));
		Associate otherAssociate = associateRepository.save(Associate.create("다른어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("기존 추억")
			.description("기존 추억에 대한 설명입니다.")
			.location(Location.builder()
				.address("기존 주소")
				.name("기존 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(authorAssociate)
			.build());
		memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(authorAssociate).build());

		CreateUpdateMemoryRequest.LocationRequest updatedLocationRequest = CreateUpdateMemoryRequest.LocationRequest.builder()
			.address("수정된 주소")
			.name("수정된 장소")
			.latitude(30.0F)
			.longitude(40.0F)
			.code(2)
			.build();

		CreateUpdateMemoryRequest.PeriodRequest updatedPeriodRequest = CreateUpdateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 9, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 9, 1, 11, 0))
			.build();

		CreateUpdateMemoryRequest request = CreateUpdateMemoryRequest.builder()
			.title("수정된 추억")
			.description("수정된 추억에 대한 설명입니다.")
			.location(updatedLocationRequest)
			.period(updatedPeriodRequest)
			.associates(List.of())
			.build();

		// when // then
		assertThatThrownBy(() -> memoryService.update(request, otherAssociate.getId(), memory.getId()))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_AUTHOR);
			});
	}

	@Test
	@DisplayName("기억을 삭제한다.")
	void deleteMemory_success() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1010L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("삭제할 추억")
			.description("삭제될 추억입니다.")
			.location(Location.builder()
				.address("삭제 주소")
				.name("삭제 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(associate)
			.build());

		// when
		memoryService.delete(memory.getId(), associate.getId());

		// then
		assertThat(memoryRepository.findByIdAndDeletedAtIsNull(memory.getId())).isEmpty();
	}

	@Test
	@DisplayName("존재하지 않는 기억을 삭제하면 예외가 발생한다.")
	void deleteMemory_memoryNotFound() {
		// given
		Long nonExistentMemoryId = 9999L;
		Long currentAssociateId = 1L; // Dummy ID

		// when // then
		assertThatThrownBy(() -> memoryService.delete(nonExistentMemoryId, currentAssociateId))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("기억의 작성자가 아닌 경우 기억을 삭제하면 예외가 발생한다.")
	void deleteMemory_notAuthor() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1011L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate authorAssociate = associateRepository.save(Associate.create("작성자어소시에이트", member, community));
		Associate otherAssociate = associateRepository.save(Associate.create("다른어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("삭제할 추억")
			.description("삭제될 추억입니다.")
			.location(Location.builder()
				.address("삭제 주소")
				.name("삭제 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(authorAssociate)
			.build());

		// when // then
		assertThatThrownBy(() -> memoryService.delete(memory.getId(), otherAssociate.getId()))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_AUTHOR);
			});
	}

	@Test
	@DisplayName("기억에 연결된 이미지를 다운로드한다.")
	void downloadImages_success() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1012L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Memory memory = memoryRepository.save(Memory.builder()
			.title("이미지 다운로드 추억")
			.description("이미지 다운로드 테스트입니다.")
			.location(Location.builder()
				.address("이미지 주소")
				.name("이미지 장소")
				.latitude(BigDecimal.valueOf(10.0F))
				.longitude(BigDecimal.valueOf(20.0F))
				.code(1)
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.of(2023, 1, 1, 10, 0))
				.endTime(LocalDateTime.of(2023, 1, 1, 11, 0))
				.build())
			.community(community)
			.associate(associate)
			.build());

		Post post = postRepository.save(Post.builder().content("이미지 포스트").memory(memory).associate(associate).build());

		PostImage postImage1 = postImageRepository.save(
			PostImage.builder().url("image_url_1.jpg").hash(Hash.builder().hash("hash1").build()).post(post).build());
		PostImage postImage2 = postImageRepository.save(
			PostImage.builder().url("image_url_2.png").hash(Hash.builder().hash("hash2").build()).post(post).build());

		// when
		DownloadImagesResponse response = memoryService.downloadImages(memory.getId());

		// then
		assertThat(response).isNotNull();
		assertThat(response.pictures()).hasSize(2);
		assertThat(response.pictures().get(0)).isEqualTo("image_url_2.png");
		assertThat(response.pictures().get(1)).isEqualTo("image_url_1.jpg");
	}

	@Test
	@DisplayName("존재하지 않는 기억의 이미지를 다운로드하면 예외가 발생한다.")
	void downloadImages_memoryNotFound() {
		// given
		Long nonExistentMemoryId = 9999L;

		// when // then
		assertThatThrownBy(() -> memoryService.downloadImages(nonExistentMemoryId))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException e = (MementoException)ex;
				assertThat(e.getErrorCode()).isEqualTo(MEMORY_NOT_FOUND);
			});
	}
}
