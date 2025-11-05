package com.memento.server.api.controller.memory.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.memento.server.api.controller.community.dto.response.AssociateResponse;
import com.memento.server.api.controller.post.dto.response.AuthorResponse;
import com.memento.server.api.service.memory.dto.Author;
import com.memento.server.api.service.memory.dto.MemoryItem;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.post.PostImage;

import lombok.Builder;

@Builder
public record ReadMemoryResponse(
	Long id,
	String title,
	String description,
	PeriodResponse period,
	LocationResponse location,
	Integer memberAmount,
	Integer pictureAmount,
	List<String> pictures,
	AuthorResponse author,
	List<AssociateResponse> associates
) {

	public static ReadMemoryResponse of(
		Memory memory,
		List<PostImage> images,
		Long associateCount,
		Author author,
		List<Associate> associates
	) {
		Event event = memory.getEvent();
		List<String> pictures = new ArrayList<>();

		for (PostImage image : images) {
			pictures.add(image.getUrl());
			if (pictures.size() >= 9)
				break;
		}

		return ReadMemoryResponse.builder()
			.id(memory.getId())
			.title(event.getTitle())
			.description(event.getDescription())
			.period(PeriodResponse.from(event.getPeriod()))
			.location(LocationResponse.from(event.getLocation()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(images.size())
			.pictures(pictures)
			.author(AuthorResponse.of(author))
			.associates(AssociateResponse.from(associates))
			.build();
	}

	public static ReadMemoryResponse of(
		MemoryItem memory,
		List<String> pictures,
		Integer associateCount
	) {
		return ReadMemoryResponse.builder()
			.id(memory.id())
			.title(memory.title())
			.description(memory.description())
			.period(PeriodResponse.from(memory.period()))
			.location(LocationResponse.from(memory.location()))
			.memberAmount(Math.toIntExact(associateCount))
			.pictureAmount(pictures.size())
			.pictures(pictures)
			.author(AuthorResponse.of(memory.associate(), memory.achievement()))
			.build();
	}
}
