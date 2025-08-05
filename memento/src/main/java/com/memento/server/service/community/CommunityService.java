package com.memento.server.service.community;

import java.util.List;

import org.springframework.stereotype.Service;

import com.memento.server.controller.community.CommunityListResponse.CommunityResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {

	public List<CommunityResponse> searchAll(Long memberId) {
		return null;
	}
}
