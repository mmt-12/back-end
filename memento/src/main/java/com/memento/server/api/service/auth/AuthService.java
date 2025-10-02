package com.memento.server.api.service.auth;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.TOKEN_NOT_VALID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.auth.dto.AuthGuestResponse;
import com.memento.server.api.controller.auth.dto.AuthMemberResponse;
import com.memento.server.api.controller.auth.dto.AuthResponse;
import com.memento.server.api.controller.auth.dto.TokenRefreshRequest;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.auth.jwt.JwtToken;
import com.memento.server.api.service.auth.jwt.JwtTokenProvider;
import com.memento.server.api.service.auth.jwt.MemberClaim;
import com.memento.server.api.service.oauth.KakaoOpenIdDecoder;
import com.memento.server.api.service.oauth.KakaoOpenIdPayload;
import com.memento.server.api.service.oauth.KakaoToken;
import com.memento.server.client.oauth.KakaoClient;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.SignInAchievementEvent;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final KakaoClient kakaoClient;
	private final JwtTokenProvider jwtTokenProvider;
	private final KakaoOpenIdDecoder kakaoOpenIdDecoder;
	private final AssociateRepository associateRepository;
	private final MemberRepository memberRepository;
	private final AchievementEventPublisher achievementEventPublisher;

	public String getAuthUrl() {
		return kakaoClient.getAuthUrl();
	}

	public AuthResponse handleAuthorizationCallback(String code) {
		KakaoToken kakaoToken = kakaoClient.getKakaoToken(code);
		KakaoOpenIdPayload openIdPayload = kakaoOpenIdDecoder.validateOpenIdToken(kakaoToken.idToken());
		Long kakaoId = Long.parseLong(openIdPayload.sub());

		return memberRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId)
			.<AuthResponse>map(member -> {
				// 커뮤니티 자동 선택
				Associate associate = associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())
					.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

				MemberClaim memberClaim = MemberClaim.builder()
					.memberId(member.getId())
					.communityId(associate.getCommunity().getId())
					.associateId(associate.getId())
					.isMember(true)
					.build();
				JwtToken token = jwtTokenProvider.createToken(memberClaim);

				achievementEventPublisher.publishSignInAchievement(SignInAchievementEvent.builder()
					.associateId(associate.getId())
					.build());
				return AuthMemberResponse.builder()
					.memberId(member.getId())
					.name(member.getName())
					.token(token)
					.build();
			})
			.orElseGet(() -> {
				MemberClaim memberClaim = MemberClaim.builder()
					.memberId(kakaoId)
					.isMember(false)
					.build();
				JwtToken token = jwtTokenProvider.createTempToken(memberClaim);

				return AuthGuestResponse.builder()
					.kakaoId(kakaoId)
					.email(openIdPayload.email())
					.token(token)
					.build();
			});
	}

	public AuthResponse refreshToken(TokenRefreshRequest request) {
		if (jwtTokenProvider.isNotValidateToken(request.refreshToken())) {
			throw new MementoException(TOKEN_NOT_VALID);
		}

		MemberClaim refreshClaim = jwtTokenProvider.extractMemberClaim(request.refreshToken());
		Member member = memberRepository.findByIdAndDeletedAtIsNull(refreshClaim.memberId())
			.orElseThrow(() -> new MementoException(TOKEN_NOT_VALID));
		Associate associate = associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));

		MemberClaim memberClaim = MemberClaim.from(member, associate);
		JwtToken token = jwtTokenProvider.createToken(memberClaim);
		return AuthMemberResponse.builder()
			.memberId(member.getId())
			.name(member.getName())
			.token(token)
			.build();
	}
}
