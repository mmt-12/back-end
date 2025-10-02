package com.memento.server.api.service.guestBook;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.GuestBookFCM;
import com.memento.server.api.service.minio.MinioService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookExclusiveAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestBookService {

	private final MinioService minioService;
	private final AssociateRepository associateRepository;
	private final GuestBookRepository guestBookRepository;
	private final VoiceRepository voiceRepository;
	private final EmojiRepository emojiRepository;
	private final FCMEventPublisher fcmEventPublisher;
	private final AchievementEventPublisher achievementEventPublisher;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	@Transactional
	public void create(Long communityId, Long registerId, Long associateId, GuestBookType type, Long contentId, String content) {
		Associate register = validAssociate(communityId, registerId);
		Associate associate = validAssociate(communityId, associateId);

		GuestBook guestBook = null;
		if(type.equals(GuestBookType.TEXT)){
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(content)
				.type(type)
				.build();
		}else if(type.equals(GuestBookType.VOICE)){
			Voice voice = voiceRepository.findByIdAndDeletedAtIsNull(contentId)
				.orElseThrow(() -> new MementoException(ErrorCodes.VOICE_NOT_FOUND));
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(voice.getUrl())
				.name(voice.getName())
				.type(type)
				.build();
		}else{
			Emoji emoji = emojiRepository.findByIdAndDeletedAtIsNull(contentId)
				.orElseThrow(() -> new MementoException(ErrorCodes.EMOJI_NOT_FOUND));
			guestBook = GuestBook.builder()
				.associate(associate)
				.content(emoji.getUrl())
				.name(emoji.getName())
				.type(type)
				.build();
		}
		
		GuestBook result = guestBookRepository.save(guestBook);

		achievementEventPublisher.publishGuestBookAchievement(GuestBookAchievementEvent.from(register.getId(), result.getId(), GuestBookAchievementEvent.Type.COUNT));
		if(result.getType().equals(GuestBookType.TEXT)){
			achievementEventPublisher.publishGuestBookAchievement(GuestBookAchievementEvent.from(register.getId(), result.getId(), GuestBookAchievementEvent.Type.WORD));
		}
		if(associate.getCommunity().getName().equals("SSAFY 12기 12반")){
			achievementEventPublisher.publishGuestBookExclusiveAchievement(GuestBookExclusiveAchievementEvent.from(
				register.getId(),
				register.getMember().getBirthday(),
				associate.getMember().getBirthday()
			));
		}

		fcmEventPublisher.publishNotification(GuestBookFCM.from(associateId));
	}

	@Transactional
	public void createBubble(Long communityId, Long registerId,Long associateId, MultipartFile voice) {
		Associate register = validAssociate(communityId, registerId);
		Associate associate = validAssociate(communityId, associateId);

		String url = saveVoice(associate, voice);

		GuestBook result = guestBookRepository.save(GuestBook.builder()
				.associate(associate)
				.content(url)
				.type(GuestBookType.VOICE)
				.build());

		achievementEventPublisher.publishGuestBookAchievement(GuestBookAchievementEvent.from(associateId, result.getId(), GuestBookAchievementEvent.Type.COUNT));
		if(associate.getCommunity().getId() == 1L){
			achievementEventPublisher.publishGuestBookExclusiveAchievement(GuestBookExclusiveAchievementEvent.from(
				register.getId(),
				register.getMember().getBirthday(),
				associate.getMember().getBirthday()
			));
		}

		fcmEventPublisher.publishNotification(GuestBookFCM.from(associateId));
	}

	public SearchGuestBookResponse search(Long communityId, Long associateId, int size, Long cursor) {
		Associate associate = validAssociate(communityId, associateId);

		Pageable pageable = PageRequest.of(0,size+1);

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

		List<SearchGuestBookResponse.GuestBook> guestBooks = guestBookList.stream().limit(size)
			.map(g -> SearchGuestBookResponse.GuestBook.builder()
				.id(g.getId())
				.type(g.getType())
				.content(g.getContent())
				.name(g.getName())
				.createdAt(g.getCreatedAt())
				.build())
			.toList();

		return SearchGuestBookResponse.builder()
			.guestBooks(guestBooks)
			.nextCursor(lastCursor)
			.hasNext(hasNext)
			.build();
	}

	@Transactional
	public void delete(Long guestBookId) {
		GuestBook guestBook = guestBookRepository.findByIdAndDeletedAtNull(guestBookId)
			.orElseThrow(() -> new MementoException(ErrorCodes.GUESTBOOK_NOT_EXISTENCE));

		guestBook.markDeleted();
	}

	public String saveVoice(Associate associate, MultipartFile voice) {
		String url = minioService.createFile(voice, MinioProperties.FileType.VOICE);

		return voiceRepository.save(Voice.builder()
			.associate(associate)
			.url(url)
			.temporary(true)
			.build()).getUrl();
	}
}
