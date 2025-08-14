package com.memento.server.spring.api.service.memory;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.memory.dto.CreateMemoryRequest;
import com.memento.server.api.controller.memory.dto.CreateMemoryResponse;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryRequest;
import com.memento.server.api.controller.memory.dto.ReadAllMemoryResponse;
import com.memento.server.api.service.memory.MemoryService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryAssociate;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Hash;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;

@SpringBootTest
@Transactional
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
	private EventRepository eventRepository;

	@Test
	@DisplayName("모든 기억을 조회한다.")
	void readAll() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Event event1 = eventRepository.save(
			Event.builder()
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
		Event event2 = eventRepository.save(Event.builder()
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
		Event event3 = eventRepository.save(Event.builder()
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

		Memory memory1 = memoryRepository.save(Memory.builder().event(event1).build());
		Memory memory2 = memoryRepository.save(Memory.builder().event(event2).build());
		Memory memory3 = memoryRepository.save(Memory.builder().event(event3).build());

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
		ReadAllMemoryRequest request = ReadAllMemoryRequest.builder()
			.cursor(null)
			.size(10)
			.keyword(null)
			.startDate(null)
			.endDate(null)
			.build();
		ReadAllMemoryResponse response = memoryService.readAll(community.getId(), request);

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
		assertThat(response.cursor()).isEqualTo(memory1.getId());
	}

	@Test
	@DisplayName("커서 기반으로 기억을 조회한다.")
	void readAll_withCursor() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Event event1 = eventRepository.save(
			Event.builder()
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
		Event event2 = eventRepository.save(Event.builder()
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
		Event event3 = eventRepository.save(Event.builder()
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
		Event event4 = eventRepository.save(Event.builder()
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
		Event event5 = eventRepository.save(Event.builder()
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

		Memory memory1 = memoryRepository.save(Memory.builder().event(event1).build());
		Memory memory2 = memoryRepository.save(Memory.builder().event(event2).build());
		Memory memory3 = memoryRepository.save(Memory.builder().event(event3).build());
		Memory memory4 = memoryRepository.save(Memory.builder().event(event4).build());
		Memory memory5 = memoryRepository.save(Memory.builder().event(event5).build());

		// when
		ReadAllMemoryRequest request1 = ReadAllMemoryRequest.builder()
			.cursor(null)
			.size(2)
			.keyword(null)
			.startDate(null)
			.endDate(null)
			.build();
		ReadAllMemoryResponse response1 = memoryService.readAll(community.getId(), request1);

		// then
		assertThat(response1.memories()).hasSize(2);
		assertThat(response1.memories().get(0).title()).isEqualTo("추억5");
		assertThat(response1.memories().get(1).title()).isEqualTo("추억4");
		assertThat(response1.hasNext()).isTrue();
		assertThat(response1.cursor()).isEqualTo(memory4.getId());

		// when
		ReadAllMemoryRequest request2 = ReadAllMemoryRequest.builder()
			.cursor(response1.cursor())
			.size(2)
			.keyword(null)
			.startDate(null)
			.endDate(null)
			.build();
		ReadAllMemoryResponse response2 = memoryService.readAll(community.getId(), request2);

		// then
		assertThat(response2.memories()).hasSize(2);
		assertThat(response2.memories().get(0).title()).isEqualTo("추억3");
		assertThat(response2.memories().get(1).title()).isEqualTo("추억2");
		assertThat(response2.hasNext()).isTrue();
		assertThat(response2.cursor()).isEqualTo(memory2.getId());

		// when
		ReadAllMemoryRequest request3 = ReadAllMemoryRequest.builder()
			.cursor(response2.cursor())
			.size(2)
			.keyword(null)
			.startDate(null)
			.endDate(null)
			.build();
		ReadAllMemoryResponse response3 = memoryService.readAll(community.getId(), request3);

		// then
		assertThat(response3.memories()).hasSize(1);
		assertThat(response3.memories().get(0).title()).isEqualTo("추억1");
		assertThat(response3.hasNext()).isFalse();
		assertThat(response3.cursor()).isEqualTo(memory1.getId());
	}

	@Test
	@DisplayName("키워드로 기억을 조회한다.")
	void readAll_withKeyword() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		Event event1 = eventRepository.save(
			Event.builder()
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
		Event event2 = eventRepository.save(Event.builder()
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
		Event event3 = eventRepository.save(Event.builder()
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

		memoryRepository.save(Memory.builder().event(event1).build());
		memoryRepository.save(Memory.builder().event(event2).build());
		memoryRepository.save(Memory.builder().event(event3).build());

		// when
		ReadAllMemoryRequest request = ReadAllMemoryRequest.builder()
			.cursor(null)
			.size(10)
			.keyword("여행")
			.startDate(null)
			.endDate(null)
			.build();
		ReadAllMemoryResponse response = memoryService.readAll(community.getId(), request);

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

		Event event1 = eventRepository.save(
			Event.builder()
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
		Event event2 = eventRepository.save(Event.builder()
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
		Event event3 = eventRepository.save(Event.builder()
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
		Event event4 = eventRepository.save(Event.builder()
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

		Memory memory1 = memoryRepository.save(Memory.builder().event(event1).build());
		Memory memory2 = memoryRepository.save(Memory.builder().event(event2).build());
		Memory memory3 = memoryRepository.save(Memory.builder().event(event3).build());
		Memory memory4 = memoryRepository.save(Memory.builder().event(event4).build());

		// when
		LocalDate startDate = LocalDate.now().minusDays(1);
		LocalDate endDate = LocalDate.now();
		ReadAllMemoryRequest request = ReadAllMemoryRequest.builder()
			.cursor(null)
			.size(10)
			.keyword(null)
			.startDate(startDate)
			.endDate(endDate)
			.build();
		ReadAllMemoryResponse response = memoryService.readAll(community.getId(), request);

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

		CreateMemoryRequest.LocationRequest locationRequest = CreateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateMemoryRequest.PeriodRequest periodRequest = CreateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateMemoryRequest request = CreateMemoryRequest.builder()
			.title("새로운 추억")
			.description("새로운 추억에 대한 설명입니다.")
			.location(locationRequest)
			.period(periodRequest)
			.associates(List.of(otherAssociate.getId()))
			.build();

		// when
		CreateMemoryResponse response = memoryService.create(community.getId(), associate.getId(), request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.memoryId()).isNotNull();

		Memory foundMemory = memoryRepository.findById(response.memoryId()).orElseThrow();
		assertThat(foundMemory.getEvent().getTitle()).isEqualTo("새로운 추억");
		assertThat(foundMemory.getEvent().getDescription()).isEqualTo("새로운 추억에 대한 설명입니다.");
		assertThat(foundMemory.getEvent().getLocation().getAddress()).isEqualTo("테스트 주소");
		assertThat(foundMemory.getEvent().getPeriod().getStartTime()).isEqualTo(LocalDateTime.of(2024, 8, 1, 10, 0));

		List<MemoryAssociate> memoryAssociates = memoryAssociateRepository.findAllByMemory(foundMemory);
		assertThat(memoryAssociates).hasSize(2); // original associate + otherAssociate
		assertThat(memoryAssociates).extracting(MemoryAssociate::getAssociate)
			.containsExactlyInAnyOrder(associate, otherAssociate);
	}

	@Test
	@DisplayName("존재하지 않는 참여자로 기억을 생성하면 예외가 발생한다.")
	void createMemory_associateNotFound() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1005L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		CreateMemoryRequest.LocationRequest locationRequest = CreateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateMemoryRequest.PeriodRequest periodRequest = CreateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateMemoryRequest request = CreateMemoryRequest.builder()
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
				assertThat(e.getErrorCode()).isEqualTo(ASSOCIATE_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("존재하지 않는 커뮤니티로 기억을 생성하면 예외가 발생한다.")
	void createMemory_communityNotFound() {
		// given
		Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1006L));
		Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
		Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

		CreateMemoryRequest.LocationRequest locationRequest = CreateMemoryRequest.LocationRequest.builder()
			.address("테스트 주소")
			.name("테스트 장소")
			.latitude(10.0F)
			.longitude(20.0F)
			.code(1)
			.build();

		CreateMemoryRequest.PeriodRequest periodRequest = CreateMemoryRequest.PeriodRequest.builder()
			.startTime(LocalDateTime.of(2024, 8, 1, 10, 0))
			.endTime(LocalDateTime.of(2024, 8, 1, 11, 0))
			.build();

		CreateMemoryRequest request = CreateMemoryRequest.builder()
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
}
