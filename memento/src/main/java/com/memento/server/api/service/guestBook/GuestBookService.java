package com.memento.server.api.service.guestBook;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestBookService {

	private final AssociateRepository associateRepository;
	private final GuestBookRepository guestBookRepository;
	private final VoiceService voiceService;
	private final VoiceRepository voiceRepository;
	private final EmojiRepository emojiRepository;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	@Transactional
	public void create(Long communityId, Long associateId, GuestBookType type, Long contentId, String content) {
		Associate associate = validAssociate(communityId, associateId);

		GuestBook guestBook = null;
		if(type.equals(GuestBookType.TEXT)){
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(content)
				.type(type)
				.build();
		}else if(type.equals(GuestBookType.VOICE)){
			Voice voice = voiceRepository.findByIdAndDeletedAtNull(contentId);
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(voice.getUrl())
				.type(type)
				.build();
		}else{
			Emoji emoji = emojiRepository.findByIdAndDeletedAtNull(contentId);
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(emoji.getUrl())
				.type(type)
				.build();
		}
		
		guestBookRepository.save(guestBook);
	}

	@Transactional
	public void createBubble(Long communityId, Long associateId, MultipartFile voice) {
		Associate associate = validAssociate(communityId, associateId);

		Voice saveVoice = voiceService.saveVoice(associate, voice);

		if (saveVoice == null) {
			throw new MementoException(ErrorCodes.VOICE_SAVE_FAIL);
		}

		guestBookRepository.save(GuestBook.builder()
				.associate(associate)
				.content(saveVoice.getUrl())
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

	@Transactional
	public void delete(Long guestBookId) {
		GuestBook guestBook = guestBookRepository.findByIdAndDeletedAtNull(guestBookId)
			.orElseThrow(() -> new MementoException(ErrorCodes.GUESTBOOK_NOT_EXISTENCE));

		guestBook.markDeleted();
	}
}
