package com.memento.server.spring.domain.community;

import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
class CommunityRepositoryTest {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("ID와 삭제되지 않은 상태로 커뮤니티를 조회한다")
    void findByIdAndDeletedAtIsNull_커뮤니티를_ID로_조회한다() {
        // given
        Member member = memberRepository.save(Member.create("테스트멤버", "test@test.com", LocalDate.of(1990, 1, 1), 1007L));
        Community community = communityRepository.save(Community.builder()
            .name("Test Community")
            .member(member)
            .build());

        // when
        Optional<Community> foundCommunity = communityRepository.findByIdAndDeletedAtIsNull(community.getId());

        // then
        assertThat(foundCommunity).isPresent();
        assertThat(foundCommunity.get().getId()).isEqualTo(community.getId());
    }
}
