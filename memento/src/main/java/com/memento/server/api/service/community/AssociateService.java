package com.memento.server.api.service.community;

import static com.memento.server.common.error.ErrorCodes.COMMUNITY_NOT_FOUND;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssociateService {

	private final AssociateRepository repository;
	private final CommunityRepository communityRepository;

	public AssociateListResponse searchAll(
		Long communityId,
		String keyword,
		Long cursor,
		Integer size
	) {
		Optional<Community> communityOptional = communityRepository.findById(communityId);
		if (communityOptional.isEmpty()) {
			throw new MementoException(COMMUNITY_NOT_FOUND);
		}

		List<Associate> associates = repository.findAllByCommunityIdAndKeywordWithCursor(
			communityId,
			keyword,
			cursor,
			PageRequest.of(0, size + 1)
		);

		return AssociateListResponse.from(associates, communityOptional.get(), size);
	}

	public CommunityListResponse searchAllMyAssociate(Long memberId) {
		List<Associate> associates = repository.findAllByMemberId(memberId);

		return CommunityListResponse.from(associates);
	}
}
