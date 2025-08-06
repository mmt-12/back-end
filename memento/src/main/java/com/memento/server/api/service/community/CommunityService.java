package com.memento.server.api.service.community;

import java.util.List;

import org.springframework.stereotype.Service;

import com.memento.server.api.controller.community.dto.CommunityListResponse.CommunityResponse;
import com.memento.server.domain.community.CommunityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {

	private final CommunityRepository repository;

	public List<CommunityResponse> searchAll(Long memberId) {
		return null;
	}
}
