package com.memento.server.api.service.community;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.community.dto.AssociateListResponse;
import com.memento.server.domain.community.AssociateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssociateService {

	private final AssociateRepository repository;

	public AssociateListResponse searchAll(Long communityId, String keyword, Long cursor, Integer size) {
		return null;
	}
}
