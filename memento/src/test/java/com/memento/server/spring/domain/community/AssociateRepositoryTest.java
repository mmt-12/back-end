package com.memento.server.spring.domain.community;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
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
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

@Transactional
public class AssociateRepositoryTest extends IntegrationsTestSupport {

    @Autowired
    private AssociateRepository associateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private MemoryAssociateRepository memoryAssociateRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("findByMemberIdAndDeletedAtIsNull")
    void findByMemberIdAndDeletedAtIsNull() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member)
            .community(community)
            .build());

        // when
        Optional<Associate> foundAssociate = associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId());

        // then
        assertThat(foundAssociate.get().getId()).isEqualTo(associate.getId());
    }

    @Test
    @DisplayName("포스트 생성자를 조회한다.")
    void findByPostId() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
        Member member3 = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1009L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Associate associate2 = associateRepository.save(Associate.create("테스트어소시에이트", member2, community));
        Associate associate3 = associateRepository.save(Associate.create("테스트어소시에이트", member3, community));

        Event event = eventRepository.save(Event.builder()
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
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate2).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate3).build());
        Post post = postRepository.save(Post.builder()
            .content("content")
            .memory(memory)
            .associate(associate)
            .build());

        // when
        Optional<Associate> foundAssociate = associateRepository.findByPostId(post.getId());

        // then
        assertThat(foundAssociate).isPresent();
        assertThat(foundAssociate.get()).isEqualTo(associate);
    }

    @Test
    @DisplayName("기억의 참여자들을 조회한다.")
    void findAllByMemoryId() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
        Member member3 = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1009L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Associate associate2 = associateRepository.save(Associate.create("테스트어소시에이트", member2, community));
        Associate associate3 = associateRepository.save(Associate.create("테스트어소시에이트", member3, community));

        Event event = eventRepository.save(Event.builder()
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
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate2).build());
        memoryAssociateRepository.save(MemoryAssociate.builder().memory(memory).associate(associate3).build());

        // when
        List<Associate> foundAssociates = associateRepository.findAllByMemoryId(memory.getId());

        // then
        assertThat(foundAssociates).hasSize(3);
    }

    @Test
    @DisplayName("ID와 삭제되지 않은 상태로 연관 관계를 조회한다")
    void findByIdAndDeletedAtIsNull_ID와_삭제되지_않은_상태로_연관_관계를_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member)
            .community(community)
            .build());

        // when
        Optional<Associate> foundAssociate = associateRepository.findByIdAndDeletedAtIsNull(associate.getId());

        // then
        assertThat(foundAssociate).isPresent();
        assertThat(foundAssociate.get().getId()).isEqualTo(associate.getId());
    }

    @Test
    @DisplayName("회원 ID와 삭제되지 않은 상태로 연관 관계 목록을 조회한다")
    void findAllByMemberIdAndDeletedAtIsNull_회원_ID와_삭제되지_않은_상태로_연관_관계_목록을_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member)
            .community(community)
            .build());

        // when
        List<Associate> foundAssociates = associateRepository.findAllByMemberIdAndDeletedAtIsNull(member.getId());

        // then
        assertThat(foundAssociates).hasSize(1);
        assertThat(foundAssociates).extracting(Associate::getMember).extracting(Member::getId)
                .containsOnly(member.getId());
    }

    @Test
    @DisplayName("커뮤니티 ID와 키워드로 연관 관계 목록을 조회한다")
    void findAllByCommunityIdIdAndKeywordWithCursor_커뮤니티_ID와_키워드로_연관_관계_목록을_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("테스트멤버2", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member)
            .community(community)
            .build());
        associateRepository.save(Associate.builder()
            .nickname("Apple")
            .member(member2)
            .community(community)
            .build());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Associate> foundAssociates = associateRepository.findAllByCommunityIdAndKeywordWithCursor(
                community.getId(),
                "Apple",
                null,
                pageable
        );

        // then
        assertThat(foundAssociates).hasSize(1);
        assertThat(foundAssociates.get(0).getNickname()).containsIgnoringCase("Apple");
    }

    @Test
    @DisplayName("ID 목록과 삭제되지 않은 상태로 연관 관계 목록을 조회한다")
    void findAllByIdInAndDeletedAtIsNull_ID_목록과_삭제되지_않은_상태로_연관_관계_목록을_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("Apple", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member)
            .community(community)
            .build());
        Associate associate2 = associateRepository.save(Associate.builder()
            .nickname("Associate 1")
            .member(member2)
            .community(community)
            .build());

        List<Long> associateIds = List.of(associate.getId(), associate2.getId());

        // when
        List<Associate> foundAssociates = associateRepository.findAllByIdInAndDeletedAtIsNull(associateIds);

        // then
        assertThat(foundAssociates).hasSize(2);
        assertThat(foundAssociates).extracting(Associate::getId)
                .containsExactlyInAnyOrder(associate.getId(), associate2.getId());
    }

    @Test
    @DisplayName("커뮤니티로 조회한다.")
    void findAllByCommunityId() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
        Member member3 = memberRepository.save(Member.create("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Community community2 = communityRepository.save(Community.create("테스트커뮤니티", member2));
        associateRepository.save(Associate.create("나나", member, community));
        associateRepository.save(Associate.create("나나", member2, community2));
        associateRepository.save(Associate.create("다다", member3, community));

        // when
        List<Associate> associates = associateRepository.findAllByCommunityId(community.getId());

        // then
        assertThat(associates.size()).isEqualTo(2);
    }

	@Test
	@DisplayName("삭제되지 않은 associate를 id로 조회한다.")
	void findByIdAndDeletedAtIsNull() {
		// given
		Fixtures fixtures = createFixtures();
		Long associateId = fixtures.associate.getId();

		// when
		Optional<Associate> foundAssociate = associateRepository.findByIdAndDeletedAtIsNull(associateId);

		// then
		assertThat(foundAssociate).isPresent();
		assertThat(foundAssociate.get()).isEqualTo(fixtures.associate);
	}

    private record Fixtures(
        Member member,
        Community community,
        Associate associate
    ) {

    }

    private Fixtures createFixtures() {
        Member member = MemberFixtures.member();
        Community community = CommunityFixtures.community(member);
		Associate associate = AssociateFixtures.associate(member, community);

		Member savedMember = memberRepository.save(member);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate = associateRepository.save(associate);

		return new Fixtures(savedMember, savedCommunity, savedAssociate);
	}
}
