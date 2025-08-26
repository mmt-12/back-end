package com.memento.server.spring.domain.memory;

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
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.event.EventFixtures;
import com.memento.server.memory.MemoryFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
class MemoryRepositoryTest {

    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private AssociateRepository associateRepository;

    @Test
    @DisplayName("ID와 삭제되지 않은 상태로 메모리를 조회한다")
    void findByIdAndDeletedAtIsNull_메모리를_ID로_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Event event = eventRepository.save(
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
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());

        // when
        Optional<Memory> foundMemory = memoryRepository.findByIdAndDeletedAtIsNull(memory.getId());

        // then
        assertThat(foundMemory).isPresent();
        assertThat(foundMemory.get().getId()).isEqualTo(memory.getId());
    }

    @Test
    @DisplayName("조건에 따라 메모리 목록을 조회한다")
    void findAllByConditions_조건에_따라_메모리_목록을_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Event event = eventRepository.save(
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
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Memory> memories = memoryRepository.findAllByConditions(
                community.getId(),
                null,
                null,
                null,
                null,
                pageable
        );

        // then
        assertThat(memories).hasSize(1);
        assertThat(memories.get(0).getId()).isEqualTo(memory.getId());
    }

    @Test
    @DisplayName("조건에 따라 키워드로 메모리 목록을 조회한다")
    void findAllByConditions_키워드로_메모리_목록을_조회한다() {
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Event event = eventRepository.save(
            Event.builder()
                .title("Keyword")
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
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Memory> memories = memoryRepository.findAllByConditions(
                community.getId(),
                "Keyword",
                null,
                null,
                null,
                pageable
        );

        // then
        assertThat(memories).hasSize(1);
        assertThat(memories.get(0).getId()).isEqualTo(memory.getId());
    }

    @Test
    @DisplayName("조건에 따라 기간으로 메모리 목록을 조회한다")
    void findAllByConditions_기간으로_메모리_목록을_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1000L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Event event = eventRepository.save(
            Event.builder()
                .title("keyword")
                .description("내용1")
                .location(Location.builder()
                    .address("주소1")
                    .name("장소1")
                    .latitude(BigDecimal.valueOf(1.0))
                    .longitude(BigDecimal.valueOf(1.0))
                    .code(1)
                    .build())
                .period(Period.builder()
                    .startTime(LocalDateTime.of(2023, 1, 15, 0, 0))
                    .endTime(LocalDateTime.of(2023, 1, 15, 23, 59))
                    .build())
                .community(community)
                .associate(associate)
                .build());
        Memory memory = memoryRepository.save(Memory.builder().event(event).build());
        Pageable pageable = PageRequest.of(0, 10);

        // when
        List<Memory> memories = memoryRepository.findAllByConditions(
                community.getId(),
                null,
                LocalDateTime.of(2023, 1, 1, 0, 0),
                LocalDateTime.of(2023, 1, 31, 23, 59),
                null,
                pageable
        );

        // then
        assertThat(memories).hasSize(1);
        assertThat(memories.get(0).getId()).isEqualTo(memory.getId());
    }
}
