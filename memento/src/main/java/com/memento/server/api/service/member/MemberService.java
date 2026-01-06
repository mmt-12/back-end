package com.memento.server.api.service.member;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMBER_SECRET_INVALID;
import static com.memento.server.common.error.ErrorCodes.SIGNUP_TOKEN_INVALID;
import static com.memento.server.common.error.ErrorCodes.SING_IN_FAIL;

import com.memento.server.api.controller.member.dto.EmailCheckResponse;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.auth.dto.AuthMemberResponse;
import com.memento.server.api.controller.auth.dto.AuthResponse;
import com.memento.server.api.controller.member.dto.MemberNormalSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.controller.member.dto.MemberSignUpResultRequest;
import com.memento.server.api.controller.member.dto.SignInRequest;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.email.EmailEventPublisher;
import com.memento.server.api.service.email.dto.event.SignupRequestEvent;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.AssociateFCM;
import com.memento.server.api.service.fcm.dto.event.SignupResultFCM;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.AssociateStats;
import com.memento.server.domain.community.AssociateStatsRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.signup.SignupPendingRepository;

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
	private final PasswordEncoder passwordEncoder;
	private final SignupPendingRepository signupPendingRepository;
	private final EmailEventPublisher emailEventPublisher;

	public EmailCheckResponse checkDuplicateEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			return EmailCheckResponse.of(false);
		}
		return EmailCheckResponse.of(true);
	}

	@Transactional
	public MemberSignUpResponse signUp(Long kakaoId, MemberSignUpRequest request) {
		// secret 검사
		if (!request.secret().equals("오렌지")) {
			throw new MementoException(MEMBER_SECRET_INVALID);
		}

		// email 중복 검사
		Optional<Member> memberOptional = memberRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId);
		if (memberOptional.isPresent()) {
			throw new MementoException(MEMBER_DUPLICATE);
		}

		// 동일인물 검사
		Optional<Member> memberBirthdayOptional = memberRepository.findByBirthday(request.birthday());
		if (memberBirthdayOptional.isPresent()) {
			throw new MementoException(MEMBER_DUPLICATE);
		}

		Member member = memberRepository.save(
			Member.createKakao(request.name(), request.email(), request.birthday(), kakaoId));

		// 커뮤니티 자동 가입
		Optional<Community> communityOptional = communityRepository.findByIdAndDeletedAtIsNull(1L);
		Community community = communityOptional.orElse(
			communityRepository.save(Community.create("SSAFY 12기 12반", member)));
		Associate associate = associateRepository.save(Associate.create(request.name(), member, community));
		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.consecutiveAttendanceDays(1)
			.lastAttendedAt(LocalDateTime.now())
			.build());

		MemberClaim memberClaim = MemberClaim.of(member, associate);
		JwtToken token = jwtTokenProvider.createToken(memberClaim);

		fcmEventPublisher.publishNotification(
			AssociateFCM.from(associate.getNickname(), community.getId(), associate.getId()));
		return MemberSignUpResponse.from(member, token);
	}

	@Transactional
	public void normalSignUp(MemberNormalSignUpRequest request) {
		// secret 검사
		if (!request.secret().equals("오렌지")) {
			throw new MementoException(MEMBER_SECRET_INVALID);
		}

		// email 중복 검사
		if (memberRepository.existsByEmail(request.email())) {
			throw new MementoException(MEMBER_EMAIL_DUPLICATE);
		}

		// 동일인물 검사
		Optional<Member> memberBirthdayOptional = memberRepository.findByBirthday(request.birthday());
		if (memberBirthdayOptional.isPresent()) {
			throw new MementoException(MEMBER_DUPLICATE);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(request.password());

		// Member 저장 (상태: WAIT)
		Member member = memberRepository.save(
			Member.createNormal(request.name(), encodedPassword, request.email(), request.birthday()));

		// Redis에 fcmToken과 보안 토큰 저장
		String securityToken = signupPendingRepository.save(member.getId(), request.fcmToken());

		// Admin에게 이메일 전송 (비동기)
		emailEventPublisher.publish(
			SignupRequestEvent.of(member.getId(), request.name(), request.email(), request.birthday(), securityToken)
		);
	}

	@Transactional
	public void signUpResult(MemberSignUpResultRequest request) {
		// 토큰 검증
		if (!signupPendingRepository.verifyToken(request.memberId(), request.token())) {
			throw new MementoException(SIGNUP_TOKEN_INVALID);
		}

		Member member = memberRepository.findByIdAndDeletedAtIsNull(request.memberId())
			.orElseThrow(() -> new MementoException(MEMBER_NOT_FOUND));

		// Redis에서 fcmToken 조회
		Optional<String> fcmTokenOptional = signupPendingRepository.findFcmTokenByMemberId(request.memberId());

		if (request.isReject()) {
			member.signUpReject();

			// FCM으로 거절 알림 전송
			fcmTokenOptional.ifPresent(fcmToken ->
				fcmEventPublisher.publishNotification(SignupResultFCM.rejected(fcmToken))
			);

			// Redis에서 삭제
			signupPendingRepository.deleteByMemberId(request.memberId());
			return;
		}

		member.signUpApprove();

		// 커뮤니티 자동 가입
		Optional<Community> communityOptional = communityRepository.findByIdAndDeletedAtIsNull(1L);
		Community community = communityOptional.orElse(
			communityRepository.save(Community.create("SSAFY 12기 12반", member)));
		Associate associate = associateRepository.save(Associate.create(member.getName(), member, community));
		associateStatsRepository.save(AssociateStats.builder()
			.associate(associate)
			.consecutiveAttendanceDays(1)
			.lastAttendedAt(LocalDateTime.now())
			.build());

		// 다른 멤버들에게 새 멤버 가입 알림
		fcmEventPublisher.publishNotification(
			AssociateFCM.from(associate.getNickname(), community.getId(), associate.getId()));

		// FCM으로 승인 알림 전송
		fcmTokenOptional.ifPresent(fcmToken ->
			fcmEventPublisher.publishNotification(SignupResultFCM.accepted(fcmToken))
		);

		// Redis에서 삭제
		signupPendingRepository.deleteByMemberId(request.memberId());
	}

	public AuthResponse signIn(SignInRequest request) {
		Member member = memberRepository.findByEmail(request.email())
			.orElseThrow(() -> new MementoException(MEMBER_NOT_FOUND));

		boolean matches = passwordEncoder.matches(request.password(), member.getPassword());
		if (!matches) {
			throw new MementoException(SING_IN_FAIL);
		}

		// 커뮤니티 자동 선택
		Associate associate = associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

		MemberClaim memberClaim = MemberClaim.of(member, associate);
		JwtToken token = jwtTokenProvider.createToken(memberClaim);

		return AuthMemberResponse.of(member.getId(), member.getName(), token);
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
