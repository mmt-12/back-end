package com.memento.server.config.filter;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberClaimValidator {

	private final MemberRepository memberRepository;
	private final CommunityRepository communityRepository;
	private final AssociateRepository associateRepository;

	public boolean isValid(MemberClaim memberClaim) {
		Long memberId = memberClaim.memberId();
		Long communityId = memberClaim.communityId();
		Long associateId = memberClaim.associateId();

		Optional<Member> memberOptional = memberRepository.findByIdAndDeletedAtIsNull(memberId);
		Optional<Community> communityOptional = communityRepository.findByIdAndDeletedAtIsNull(communityId);
		Optional<Associate> associateOptional = associateRepository.findByIdAndDeletedAtIsNull(associateId);

		if (memberId != null && memberOptional.isEmpty()) {
			return false;
		}
		if (communityId != null && communityOptional.isEmpty()) {
			return false;
		}
		if (associateId != null) {
			return associateOptional.isPresent() &&
				associateOptional.get().getMember().getId().equals(memberId) &&
				associateOptional.get().getCommunity().getId().equals(communityId);
		}
		return true;
	}
}
