package com.memento.server.service.community;

import org.springframework.stereotype.Service;

import com.memento.server.controller.community.AssociateListResponse;
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
