package com.memento.server.spring.domain.community;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

@Transactional
public class AssociateRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Test
	@DisplayName("삭제되지 않은 associate를 id로 조회한다.")
	void findByIdAndDeletedAtIsNull() {
		// given
		Associate associate = createAndSaveAssociate();
		Long associateId = associate.getId();
		
		// when
		Optional<Associate> foundAssociate = associateRepository.findByIdAndDeletedAtIsNull(associateId);

		// then
		assertThat(foundAssociate).isPresent();
		assertThat(foundAssociate.get().getId()).isEqualTo(associateId);
		assertThat(foundAssociate.get().getDeletedAt()).isNull();
	}

	private Associate createAndSaveAssociate() {
		Member member = MemberFixtures.member();
		Member savedMember = memberRepository.save(member);

		Community community = CommunityFixtures.communityWithMember(savedMember);
		Community savedCommunity = communityRepository.save(community);

		Associate associate = AssociateFixtures.associateWithMemberAndCommunity(savedMember, savedCommunity);
		return associateRepository.save(associate);
	}
}
