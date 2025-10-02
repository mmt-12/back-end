package com.memento.server.api.service.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateExclusiveAchievementEvent;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.AssociateStats;
import com.memento.server.domain.community.AssociateStatsRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

	private final MemberRepository memberRepository;
	private final CommunityRepository communityRepository;
	private final AssociateRepository associateRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final FCMEventPublisher fcmEventPublisher;
	private final AchievementEventPublisher achievementEventPublisher;
	private final AssociateStatsRepository associateStatsRepository;

	@Transactional
	public MemberSignUpResponse signUp(Long kakaoId, String name, String email, LocalDate birthday) {
		Optional<Member> memberOptional = memberRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId);
		if (memberOptional.isPresent()) {
			throw new MementoException(MEMBER_DUPLICATE);
		}

		Member member = memberRepository.save(Member.create(name, email, birthday, kakaoId));

		// 커뮤니티 자동 가입
		Optional<Community> communityOptional = communityRepository.findByIdAndDeletedAtIsNull(1L);
		Community community = communityOptional.orElse(
			communityRepository.save(Community.create("SSAFY 12기 12반", member)));
		Associate associate = associateRepository.save(Associate.create(name, member, community));
		associateStatsRepository.save(AssociateStats.builder()
				.associate(associate)
				.consecutiveAttendanceDays(1)
				.lastAttendedAt(LocalDateTime.now())
			.build());

		MemberClaim memberClaim = MemberClaim.from(member, associate);
		JwtToken token = jwtTokenProvider.createToken(memberClaim);

		fcmEventPublisher.publishNotification(AssociateFCM.from(associate.getNickname(), community.getId(), associate.getId()));
		return MemberSignUpResponse.from(member, token);
	}

	@Transactional
	public void update(Long memberId, String name, String email) {
		Optional<Member> memberOptional = memberRepository.findByIdAndDeletedAtIsNull(memberId);
		if (memberOptional.isEmpty()) {
			throw new MementoException(MEMBER_NOT_FOUND);
		}

		Member member = memberOptional.get();
		member.update(name, email);
	}
}
