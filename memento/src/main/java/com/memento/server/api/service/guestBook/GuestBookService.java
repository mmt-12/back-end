package com.memento.server.api.service.guestBook;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestBookService {

	private final AssociateRepository associateRepository;
	private final GuestBookRepository guestBookRepository;
	private final VoiceService voiceService;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 참여자 입니다."));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new IllegalArgumentException("해당 커뮤니티의 참가자가 아닙니다.");
		}

		return associate;
	}

	public void create(Long communityId, Long associateId, GuestBookType type, Long contentId, String content) {
		Associate associate = validAssociate(communityId, associateId);

		GuestBook guestBook = null;
		if(type.equals(GuestBookType.TEXT)){
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(content)
				.type(type)
				.build();
		}else{
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(String.valueOf(contentId))
				.type(type)
				.build();
		}
		
		guestBookRepository.save(guestBook);
	}

	public void createBubble(Long communityId, Long associateId, MultipartFile voice) {
		Associate associate = validAssociate(communityId, associateId);

		Long contentId = voiceService.saveVoice(associate, voice);

		if (contentId == null) {
			throw new IllegalStateException("음성파일 저장 중 문제가 생겼습니다.");
		}

		guestBookRepository.save(GuestBook.builder()
				.associate(associate)
				.content(String.valueOf(contentId))
				.type(GuestBookType.VOICE)
				.build());
	}

	public SearchGuestBookResponse search(Long communityId, Long associateId, Pageable pageable, Long cursor) {
		Associate associate = validAssociate(communityId, associateId);

		List<GuestBook> guestBookList = guestBookRepository.findPageByAssociateId(
			associate.getId(),
			cursor,
			pageable
		);

		Long lastCursor = null;
		boolean hasNext = false;
		if(guestBookList.size() == pageable.getPageSize()){
			lastCursor = guestBookList.get(guestBookList.size() - 1).getId();
			hasNext = true;
		}

		List<SearchGuestBookResponse.GuestBook> guestBooks = guestBookList.stream()
			.map(g -> SearchGuestBookResponse.GuestBook.builder()
				.id(g.getId())
				.type(g.getType())
				.content(g.getContent())
				.createdAt(g.getCreatedAt())
				.build())
			.toList();

		return SearchGuestBookResponse.builder()
			.guestBooks(guestBooks)
			.cursor(lastCursor)
			.hasNext(hasNext)
			.build();
	}

	public void delete(Long communityId, Long associateId, Long guestBookId) {
		Associate associate = validAssociate(communityId, associateId);

		GuestBook guestBook = guestBookRepository.findByIdAndDeletedAtNull(guestBookId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방명록입니다."));

		guestBook.delete();
	}
}
