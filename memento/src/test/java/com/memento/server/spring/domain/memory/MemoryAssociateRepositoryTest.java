package com.memento.server.spring.domain.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

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
import com.memento.server.domain.memory.dto.MemoryAssociateCount;

@DataJpaTest
@EnableJpaAuditing
class MemoryAssociateRepositoryTest {

    @Autowired
    private MemoryAssociateRepository memoryAssociateRepository;

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
    @DisplayName("메모리와 삭제되지 않은 상태로 메모리 연관 관계를 조회한다")
    void findAllByMemoryAndDeletedAtIsNull_메모리_연관_관계를_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));

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

        // when
        List<MemoryAssociate> foundAssociates = memoryAssociateRepository.findAllByMemoryAndDeletedAtIsNull(memory);

        // then
        assertThat(foundAssociates).hasSize(1);
        assertThat(foundAssociates.get(0).getMemory().getId()).isEqualTo(memory.getId());
        assertThat(foundAssociates.get(0).getAssociate().getId()).isEqualTo(associate.getId());
    }

    @Test
    @DisplayName("메모리 ID 목록으로 연관된 Associate 수를 조회한다")
    void countAssociatesByMemoryIds_연관된_Associate_수를_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Member member2 = memberRepository.save(Member.create("테스트멤버2", "test@test.com", LocalDate.of(1990, 1, 1), 1008L));
        Community community = communityRepository.save(Community.create("테스트커뮤니티", member));
        Associate associate = associateRepository.save(Associate.create("테스트어소시에이트", member, community));
        Associate associate2 = associateRepository.save(Associate.create("다른어소시에이트", member2, community));

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

        // when
        List<MemoryAssociateCount> associateCounts = memoryAssociateRepository.countAssociatesByMemoryIds(List.of(memory.getId()));

        // then
        assertThat(associateCounts).hasSize(1);
        assertThat(associateCounts).extracting("memoryId").containsExactlyInAnyOrder(memory.getId());
        assertThat(associateCounts).filteredOn(mac -> mac.memoryId().equals(memory.getId()))
                .extracting("associateCount").containsExactly(2L);
    }
}
