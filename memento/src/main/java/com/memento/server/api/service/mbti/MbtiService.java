package com.memento.server.api.service.mbti;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.mbti.dto.SearchMbtiResponse;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
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
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	public void create(Long communityId, Long from, Long to, String mbti) {
		Associate fromAssociate = validAssociate(communityId, from);
		Associate toAssociate = validAssociate(communityId, to);

		MbtiTest existing = mbtiTestRepository.findByFromAssociateIdAndToAssociateId(fromAssociate.getId(), toAssociate.getId());
		Mbti mbtiEnum;
		try {
			mbtiEnum = Mbti.valueOf(mbti);
		} catch (IllegalArgumentException e) {
			throw new MementoException(ErrorCodes.MBTI_NOT_EXISTENCE);
		}
		if (existing != null) {
			existing.updateMbti(mbtiEnum);
			return;
		}

		mbtiTestRepository.save(MbtiTest.builder()
			.fromAssociate(fromAssociate)
			.toAssociate(toAssociate)
			.mbti(mbtiEnum)
			.build());
	}

	public SearchMbtiResponse search(Long communityId, Long associateId) {
		Associate associate = validAssociate(communityId, associateId);

		Object[] counts = mbtiTestRepository.countMbtiByToAssociate(associate.getId());

		return SearchMbtiResponse.from(counts);
	}
}
