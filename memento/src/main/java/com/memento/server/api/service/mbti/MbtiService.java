package com.memento.server.api.service.mbti;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.mbti.Mbti;
import com.memento.server.domain.mbti.MbtiTest;
import com.memento.server.domain.mbti.MbtiTestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MbtiService {

	private final AssociateRepository associateRepository;
	private final MbtiTestRepository mbtiTestRepository;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자 입니다."));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new IllegalArgumentException("해당 커뮤니티의 참가자가 아닙니다.");
		}

		return associate;
	}

	public void create(Long communityId, Long from, Long to, Mbti mbti) {
		Associate fromAssociate = validAssociate(communityId, from);
		Associate toAssociate = validAssociate(communityId, to);

		MbtiTest existing = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(fromAssociate.getId(), toAssociate.getId());

		if (existing != null) {
			existing.updateMbti(mbti);
			return;
		}

		mbtiTestRepository.save(MbtiTest.builder()
			.fromAssociate(fromAssociate)
			.toAssociate(toAssociate)
			.mbti(mbti)
			.build());
	}

	public SearchMbtiResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		Object[] counts = mbtiTestRepository.countMbtiByToAssociate(associate.getId());

		return SearchMbtiResponse.from(counts);
	}
}
