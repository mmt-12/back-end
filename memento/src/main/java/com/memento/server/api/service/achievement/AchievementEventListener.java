package com.memento.server.api.service.achievement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.AchievementFCM;
import com.memento.server.client.sse.SseEmitterRepository;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.achievement.AchievementAssociate;
import com.memento.server.domain.achievement.AchievementAssociateRepository;
import com.memento.server.domain.achievement.AchievementRepository;
import com.memento.server.domain.achievement.AchievementType;
import com.memento.server.domain.achievement.CommonAchievementEvent;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateExclusiveAchievementEvent;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.AssociateStats;
import com.memento.server.domain.community.AssociateStatsRepository;
import com.memento.server.domain.community.SignInAchievementEvent;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookExclusiveAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.mbti.MbtiAchievementEvent;
import com.memento.server.domain.mbti.MbtiTestRepository;
import com.memento.server.domain.memory.MemoryAchievementEvent;
import com.memento.server.domain.memory.MemoryAssociateRepository;
import com.memento.server.domain.post.PostImageAchievementEvent;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.profileImage.ProfileImageAchievementEvent;
import com.memento.server.domain.profileImage.ProfileImageRepository;
import com.memento.server.domain.reaction.ReactionAchievementEvent;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AchievementEventListener {

	private final AssociateRepository associateRepository;
	private final AchievementRepository achievementRepository;
	private final AchievementAssociateRepository achievementAssociateRepository;
	private final AssociateStatsRepository associateStatsRepository;
	private final ProfileImageRepository profileImageRepository;
	private final MbtiTestRepository mbtiTestRepository;
	private final GuestBookRepository guestBookRepository;
	private final EventRepository eventRepository;
	private final MemoryAssociateRepository memoryAssociateRepository;
	private final PostImageRepository postImageRepository;
	private final EmojiRepository emojiRepository;
	private final VoiceRepository voiceRepository;
	private final CommentRepository commentRepository;
	private final SseEmitterRepository sseEmitterRepository;
	private final FCMEventPublisher fcmEventPublisher;

	private void getAchievement(Long associateId, Long achievementId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));

		Achievement achievement = achievementRepository.findByIdAndDeletedAtNull(achievementId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ACHIEVEMENT_NOT_EXISTENCE));

		achievementAssociateRepository.save(AchievementAssociate.builder()
			.achievement(achievement)
			.associate(associate)
			.build());
		sendAchievementSse(associate.getId(), achievement);
		fcmEventPublisher.publishNotification(AchievementFCM.of(associate.getId()));

		// 업적헌터#kill
		if(achievement.getType() == AchievementType.OPEN){
			int count = achievementAssociateRepository.countByAssociateIdAndAchievementTypeAndDeletedAtNull(associate.getId(), AchievementType.OPEN);
			int totalCount = achievementRepository.countByType(AchievementType.OPEN);
			if (!achievementAssociateRepository.existsByAchievementIdAndAssociateId(14L, associate.getId())
				&& count >= totalCount) {
				Achievement lastAchievement = achievementRepository.findByIdAndDeletedAtNull(14L)
					.orElseThrow(() -> new MementoException(ErrorCodes.ACHIEVEMENT_NOT_EXISTENCE));
				achievementAssociateRepository.save(AchievementAssociate.builder()
					.achievement(lastAchievement)
					.associate(associate)
					.build());
				sendAchievementSse(associate.getId(), lastAchievement);
				fcmEventPublisher.publishNotification(AchievementFCM.of(associate.getId()));
			}
		}
	}

	private void sendAchievementSse(Long associateId, Achievement achievement) {
		SseEmitter emitter = sseEmitterRepository.get(associateId);
		if (emitter == null) return;

		try {
			Map<String, Object> data = Map.of(
				"type", "ACHIEVE",
				"value", Map.of(
					"id", achievement.getId(),
					"name", achievement.getName(),
					"criteria", achievement.getCriteria(),
					"type", achievement.getType().name()
				)
			);

			emitter.send(SseEmitter.event()
				.name("message")
				.data(data)
			);
		} catch (Exception e) {
			sseEmitterRepository.remove(associateId);
		}
	}

	@Async
	@EventListener
	public void handleCommonAchievement(CommonAchievementEvent event){
		Long achievementId = event.achievementId();
		Long associateId = event.associateId();
		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(achievementId, associateId)){
			getAchievement(associateId, achievementId);
		}
	}

	@Async
	@EventListener
	public void handleSignInAchievement(SignInAchievementEvent event) {
		AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		LocalDate lastDate = stats.getLastAttendedAt().toLocalDate();
		LocalDateTime today = LocalDateTime.now();

		long days = ChronoUnit.DAYS.between(lastDate, today.toLocalDate());

		if (days == 1) {
			int count = stats.updateConsecutiveAttendanceDays(stats.getConsecutiveAttendanceDays());
			stats.updateLastAttendedAt(today);

			// 시간빌게이츠
			if (!achievementAssociateRepository.existsByAchievementIdAndAssociateId(1L, event.associateId())
				&& count >= 15) {
				getAchievement(event.associateId(), 1L);
			}
		}else if(days > 1){
			stats.updateConsecutiveAttendanceDays(0);
			stats.updateLastAttendedAt(today);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleProfileImageAchievement(ProfileImageAchievementEvent event){
		AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		int count = 0;
		switch (event.type()){
			// 변검술사
			case REGISTERED:
				count = stats.updateRegisteredProfileImageCount(profileImageRepository.countByAssociateIdAndDeletedAtNull(event.associateId()));

				if (!achievementAssociateRepository.existsByAchievementIdAndAssociateId(8L, event.associateId())
					&& count >= 20) {
					getAchievement(event.associateId(), 8L);
				}
				break;
			// 파파라치
			case UPLOADED:
				count = stats.updateUploadedProfileImageCount(profileImageRepository.countByRegistrantIdAndDeletedAtNull(event.associateId()));

				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(9L, event.associateId())
					&& count >= 30){
					getAchievement(event.associateId(), 9L);
				}
				break;
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleMbtiAchievement(MbtiAchievementEvent event) {
		AssociateStats fromStats = associateStatsRepository.findByAssociateId(event.fromAssociateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));
		AssociateStats toStats = associateStatsRepository.findByAssociateId(event.toAssociateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		// 관상가
		int count = fromStats.updateMbtiTestCount(mbtiTestRepository.countByFromAssociateId(event.fromAssociateId()));
		int totalAssociate = associateRepository.countByCommunityId(fromStats.getAssociate().getCommunity().getId());

		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(2L, event.fromAssociateId())
			&& count >= totalAssociate - 1){
			getAchievement(event.fromAssociateId(), 2L);
		}
		Map<String, Object> counts = mbtiTestRepository.countAllByToAssociate(event.toAssociateId());

		int totalCount = toStats.updateMbtiTestCount(((Number) counts.get("total_count")).intValue());
		int fCount = toStats.updateFMbtiCount(((Number) counts.get("f_count")).intValue());
		int tCount = toStats.updateTMbtiCount(((Number) counts.get("t_count")).intValue());

		// 다중인격
		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(3L, event.toAssociateId())
			&& totalCount >= 6){
			getAchievement(event.toAssociateId(), 3L);
		}
		// FFFFFF
		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(4L, event.toAssociateId())
			&& fCount >= 8){
			getAchievement(event.toAssociateId(), 4L);
		}
		// T발 C야?
		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(5L, event.toAssociateId())
			&& tCount >= 8) {
			getAchievement(event.toAssociateId(), 5L);
		}

	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleGuestBookAchievement(GuestBookAchievementEvent event) {
		AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		switch (event.type()){
			// 마니또
			case COUNT :
				int count = stats.updateGuestBookCount(stats.getGuestBookCount() + 1);

				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(11L, event.associateId())
					&& count >= 50){
					getAchievement(event.associateId(), 11L);
				}
				break;
			// 팅팅팅
			case WORD:
				GuestBook guestBook = guestBookRepository.findByIdAndDeletedAtNull(event.guestBookId())
					.orElseThrow(() -> new MementoException(ErrorCodes.GUESTBOOK_NOT_EXISTENCE));

				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(18L, event.associateId())
					&& guestBook.getContent().equals("팅팅팅")){
					getAchievement(event.associateId(), 18L);
				}
				break;
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleMemoryAchievement(MemoryAchievementEvent event) {
		switch (event.type()){
			case CREATE:
				AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateIds().getFirst())
					.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));
				int count = stats.updateCreatedMemoryCount(eventRepository.countByAssociateIdAndDeletedAtNull(event.associateIds().getFirst()));
				// 민들레? 노브랜드?
				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(12L, event.associateIds().getFirst())
					&& count >= 10){
					getAchievement(event.associateIds().getFirst(), 12L);
				}

				// 13일의 금요일
				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(16L, event.associateIds().getFirst())
					&& isFridayThe13th(LocalDate.now())){
					getAchievement(event.associateIds().getFirst(), 16L);
				}
				break;
			// GMG
			case JOINED:
				Map<Long, Integer> countMap = memoryAssociateRepository.countAssociatesByAssociateIdsAndDeletedAtNull(event.associateIds()).stream()
					.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Long) row[1]).intValue()
					));
				Map<Long, AssociateStats> statsMap =
					associateStatsRepository.findByAssociateIdIn(event.associateIds())
						.stream()
						.collect(Collectors.toMap(s -> s.getAssociate().getId(), s -> s));

				for(Long associateId : event.associateIds()) {
					AssociateStats curStats = statsMap.get(associateId);
					int curCount = curStats.updateJoinedMemoryCount(countMap.getOrDefault(associateId, 0));

					if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(13L, associateId)
						&& curCount >= 12) {
						getAchievement(associateId, 13L);
					}
				}
				break;
		}
	}

	//13일의 금요일 검증
	public boolean isFridayThe13th(LocalDate date) {
		if (date == null) return false;
		return date.getDayOfMonth() == 13
			&& date.getDayOfWeek() == DayOfWeek.FRIDAY;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handlePostImageAchievement(PostImageAchievementEvent event) {
		AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		long count = stats.updateUploadedPostImageCount(postImageRepository.countByAssociateId(event.associateId()));
		// 전문찍새
		if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(10L, event.associateId())
			&& count >= 100){
			getAchievement(event.associateId(), 10L);
		}
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleReactionAchievement(ReactionAchievementEvent event) {
		AssociateStats stats = associateStatsRepository.findByAssociateId(event.associateId())
			.orElseThrow(() -> new MementoException(ErrorCodes.STATS_NOT_FOUND));

		int count = 0;
		switch (event.type()){
			case REGISTRANT:
				count += emojiRepository.countByAssociateIdAndDeletedAtNull(event.associateId());
				count += voiceRepository.countByAssociateIdAndDeletedAtNull(event.associateId());
				stats.updateRegisteredReactionCount(count);

				// 리액션공장
				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(6L, event.associateId())
					&& count >= 20){
					getAchievement(event.associateId(), 6L);
				}

				String name = switch (event.reactionType()) {
					case EMOJI -> {
						Emoji emoji = emojiRepository.findByIdAndDeletedAtIsNull(event.reactionId())
							.orElseThrow(() -> new MementoException(ErrorCodes.EMOJI_NOT_FOUND));
						yield emoji.getName();
					}
					case VOICE -> {
						Voice voice = voiceRepository.findByIdAndDeletedAtIsNull(event.reactionId())
							.orElseThrow(() -> new MementoException(ErrorCodes.VOICE_NOT_FOUND));
						yield voice.getName();
					}
				};

				// 씽씽씽
				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(17L, event.associateId())
					&& name.equals("씽씽씽")){
					getAchievement(event.associateId(), 17L);
				}

				break;
			// 입에서주스가주르륵
			case USE:
				count = stats.updateUsedReactionCount(commentRepository.countByAssociateIdAndDeletedAtNull(event.associateId()));

				if(!achievementAssociateRepository.existsByAchievementIdAndAssociateId(7L, event.associateId())
					&& count >= 500){
					getAchievement(event.associateId(), 7L);
				}
				break;
		}
	}

	// 가입 전용 업적
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleAssociateExclusiveAchievement(AssociateExclusiveAchievementEvent event){
		LocalDate birthDay = event.birthDay();
		long achievementId = 0L;

		if(birthDay.isEqual(LocalDate.of(2000, 1, 25))){
			// 쿠로네코(효선)
			achievementId = 19L;
		}else if(birthDay.isEqual(LocalDate.of(1996, 12, 16))){
			// 횬딘곤듀(현진)
			achievementId = 20L;
		}else if(birthDay.isEqual(LocalDate.of(1997, 3, 20))){
			// 귀한곳에누추한분이(민우)
			achievementId = 21L;
		}else if(birthDay.isEqual(LocalDate.of(1999, 10, 13))){
			// 뤼전드(준수)
			achievementId = 22L;
		}else if(birthDay.isEqual(LocalDate.of(1999, 4, 24))){
			// 주피티(주빈)
			achievementId = 23L;
		}else if(birthDay.isEqual(LocalDate.of(1998, 7, 16))){
			// 신(도영)
			achievementId = 24L;
		}else if(birthDay.isEqual(LocalDate.of(1999, 3, 19))){
			// 그녀석(대산)
			achievementId = 25L;
		}else if(birthDay.isEqual(LocalDate.of(1998, 1, 15))){
			// 인형(정문)
			achievementId = 26L;
		}

		if(achievementId != 0L && !achievementAssociateRepository.existsByAchievementIdAndAssociateId(achievementId, event.associateId())){
			getAchievement(event.associateId(), achievementId);
		}
	}

	// guestBook 전용 업적
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleGuestBookExclusiveAchievement(GuestBookExclusiveAchievementEvent event) {
		long achievementId = 0L;
		LocalDate from = event.from();
		LocalDate to = event.to();

		if(from.isEqual(LocalDate.of(1999, 10, 13)) && to.isEqual(LocalDate.of(1997,5,20))){
			// 닥치(준수 -> 경완)
			achievementId = 27L;
		}else if((from.isEqual(LocalDate.of(1999, 10, 12)) && to.isEqual(LocalDate.of(2001,5,19)))
		|| (from.isEqual(LocalDate.of(2001, 5, 19)) && to.isEqual(LocalDate.of(1999,10,12)))){
			// ㅁㅇㅁㅇ(세학 <-> 률아)
			achievementId = 28L;
		}else if((from.isEqual(LocalDate.of(1997, 2, 18)) && to.isEqual(LocalDate.of(1997,5,20)))
			|| (from.isEqual(LocalDate.of(1997, 5, 20)) && to.isEqual(LocalDate.of(1997,2,18)))){
			// 내절친(현우 <-> 경완)
			achievementId = 29L;
		}else if((from.isEqual(LocalDate.of(1997, 12, 1)) && to.isEqual(LocalDate.of(1996,8,23)))
			|| (from.isEqual(LocalDate.of(1996, 8, 23)) && to.isEqual(LocalDate.of(1997,12,1)))){
			// GAY(승우 <-> 승근)
			achievementId = 30L;
		}else if(from.isEqual(LocalDate.of(1996, 12, 16)) && to.isEqual(LocalDate.of(1996,8,23))){
			// 드디어봐주는구나(현진 -> 승근)
			achievementId = 31L;
		}else if((from.isEqual(LocalDate.of(1999, 8, 10)) && to.isEqual(LocalDate.of(1999,12,2)))
			|| (from.isEqual(LocalDate.of(1999, 12, 2)) && to.isEqual(LocalDate.of(1999,8,10)))){
			// 현지(류현 <-> 이현)
			achievementId = 32L;
		}else if((from.isEqual(LocalDate.of(2000, 12, 30)) && to.isEqual(LocalDate.of(1999,10,13)))
			|| (from.isEqual(LocalDate.of(1999, 10, 13)) && to.isEqual(LocalDate.of(2000,12,30)))){
			// 랑이와 싹이(예슬 <-> 준수)
			achievementId = 33L;
		}

		if(achievementId != 0L && !achievementAssociateRepository.existsByAchievementIdAndAssociateId(achievementId, event.associateId())){
			getAchievement(event.associateId(), achievementId);
		}
	}
}
