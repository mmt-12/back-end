package com.memento.server.spring.api.service.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.MEMBER_SECRET_INVALID;
import static com.memento.server.common.error.ErrorCodes.SIGNUP_TOKEN_INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.controller.member.dto.EmailCheckResponse;
import com.memento.server.api.controller.member.dto.MemberNormalSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.controller.member.dto.MemberSignUpResultRequest;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.email.EmailEventPublisher;
import com.memento.server.api.service.email.dto.event.SignupRequestEvent;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.fcm.dto.event.SignupResultFCM;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.AssociateStatsRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.member.MemberType;
import com.memento.server.domain.signup.SignupPendingRepository;

@SpringBootTest
class MemberServiceTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private AssociateService associateService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateStatsRepository associateStatsRepository;

	@MockitoBean
	private SignupPendingRepository signupPendingRepository;

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@MockitoBean
	private EmailEventPublisher emailEventPublisher;

	@MockitoBean
	private org.springframework.mail.javamail.JavaMailSender javaMailSender;

	@MockitoBean
	private com.memento.server.api.service.email.EmailService emailService;

	@MockitoBean
	private com.memento.server.api.service.email.EmailEventListener emailEventListener;

	@AfterEach
	void afterEach() {
		associateStatsRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("커뮤니티 목록을 조회한다.")
	void searchAllMyCommunities() {
		// given
		Member member = memberRepository.save(Member.createKakao("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.createKakao("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.createKakao("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Member member4 = memberRepository.save(Member.createKakao("김라라", "muge@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Community community = communityRepository.save(Community.create("comm", member));
		Community community2 = communityRepository.save(Community.create("comm2", member2));
		Community community3 = communityRepository.save(Community.create("comm3", member3));
		Community community4 = communityRepository.save(Community.create("comm4", member4));
		Associate associate1 = associateRepository.save(Associate.create("가가", member, community));
		Associate associate2 = associateRepository.save(Associate.create("나나", member, community2));
		Associate associate3 = associateRepository.save(Associate.create("다다", member, community3));
		Associate associate4 = associateRepository.save(Associate.create("라라", member, community4));

		// when
		CommunityListResponse response = associateService.searchAllMyAssociate(member.getId());

		// then
		assertThat(response.communities().size()).isEqualTo(4);
	}

	@Test
	@DisplayName("어느 커뮤니티에도 가입하지 않고 커뮤니티 목록을 조회한다.")
	void searchAllMyCommunities_empty() {
		// given
		Member member = memberRepository.save(Member.createKakao("김가가", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));
		Member member2 = memberRepository.save(Member.createKakao("김나나", "muge@test.com", LocalDate.of(1990, 1, 1), 1002L));
		Member member3 = memberRepository.save(Member.createKakao("김다다", "muge@test.com", LocalDate.of(1990, 1, 1), 1003L));
		Member member4 = memberRepository.save(Member.createKakao("김라라", "muge@test.com", LocalDate.of(1990, 1, 1), 1004L));
		Community community2 = communityRepository.save(Community.create("comm2", member2));
		Community community3 = communityRepository.save(Community.create("comm3", member3));
		Community community4 = communityRepository.save(Community.create("comm4", member4));

		// when
		CommunityListResponse response = associateService.searchAllMyAssociate(member.getId());

		// then
		assertThat(response.communities().size()).isEqualTo(0);
	}

	@Test
	@DisplayName("회원 가입을 한다.")
	void signup() {
		// given
		Long kakaoId = 1001L;
		String name = "홍길동";
		String email = "hong@test.com";
		LocalDate birthday = LocalDate.of(1990, 1, 1);
		MemberSignUpRequest request = MemberSignUpRequest.builder()
			.name(name)
			.email(email)
			.birthday(birthday)
			.secret("오렌지")
			.build();

		// when
		MemberSignUpResponse response = memberService.signUp(kakaoId, request);

		// then
		Member saved = memberRepository.findByKakaoIdAndDeletedAtIsNull(kakaoId).orElseThrow();
		assertThat(saved.getName()).isEqualTo(name);
		assertThat(saved.getEmail()).isEqualTo(email);
		assertThat(response.memberId()).isEqualTo(saved.getId());
		assertThat(response.token()).isNotNull();
		assertThat(saved.getKakaoId()).isEqualTo(kakaoId);
	}

	@Test
	@DisplayName("회원 가입 시 이미 가입한 카카오 아이디라면 MEMBER_DUPLICATE 예외가 발생한다.")
	void signup_withDuplicate_throwsException() {
		// given
		Long kakaoId = 1001L;
		String name = "홍길동";
		String email = "hong@test.com";
		LocalDate birthday = LocalDate.of(1990, 1, 1);
		MemberSignUpRequest request = MemberSignUpRequest.builder()
			.name(name)
			.email(email)
			.birthday(birthday)
			.secret("오렌지")
			.build();
		memberRepository.save(Member.createKakao(name, email, birthday, kakaoId));

		// when & then
		assertThatThrownBy(() ->
			memberService.signUp(kakaoId, request)
		)
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException)ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_DUPLICATE);
			});
	}

	@Test
	@DisplayName("회원 정보를 수정한다.")
	void update() {
		// given
		Member member = memberRepository.save(Member.createKakao("홍길동", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));

		// when
		memberService.update(member.getId(), "김철수", "kim@test.com");

		// then
		Member updated = memberRepository.findByIdAndDeletedAtIsNull(member.getId()).orElseThrow();
		assertThat(updated.getName()).isEqualTo("김철수");
		assertThat(updated.getEmail()).isEqualTo("kim@test.com");
	}

	@Test
	@DisplayName("회원 정보를 수정할 때 회원 조회에 실패하면 MEMBER_NOT_FOUND 예외가 발생한다.")
	void update_withNull_throwsException() {
		// given
		Long invalidId = 9999L;

		// when & then
		assertThatThrownBy(() ->
			memberService.update(invalidId, "김철수", "kim@test.com")
		)
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException)ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_NOT_FOUND);
			});
	}

	@Test
	@DisplayName("이메일 중복 체크 시 사용 가능한 이메일이면 isDuplicate가 false를 반환한다.")
	void checkDuplicateEmail_withAvailableEmail_success() {
		// given
		String email = "new@test.com";

		// when
		EmailCheckResponse response = memberService.checkDuplicateEmail(email);

		// then
		assertThat(response.isDuplicate()).isFalse();
	}

	@Test
	@DisplayName("이메일 중복 체크 시 이미 존재하는 이메일이면 isDuplicate가 true를 반환한다.")
	void checkDuplicateEmail_withDuplicateEmail_returnsTrue() {
		// given
		String email = "existing@test.com";
		memberRepository.save(Member.createKakao("홍길동", email, LocalDate.of(1990, 1, 1), 1001L));

		// when
		EmailCheckResponse response = memberService.checkDuplicateEmail(email);

		// then
		assertThat(response.isDuplicate()).isTrue();
	}

	@Test
	@DisplayName("일반 회원가입을 한다.")
	void normalSignUp_success() {
		// given
		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1990, 1, 1))
			.secret("오렌지")
			.fcmToken("fcm-token-123")
			.build();

		// when
		memberService.normalSignUp(request);

		// then
		Member saved = memberRepository.findByEmail("hong@test.com").orElseThrow();
		assertThat(saved.getName()).isEqualTo("홍길동");
		assertThat(saved.getType()).isEqualTo(MemberType.WAIT);

		// Redis에 fcmToken이 저장되었는지 확인
		verify(signupPendingRepository, times(1)).save(any(), any());

		// 이메일 이벤트가 발행되었는지 확인
		verify(emailEventPublisher, times(1)).publish(any(SignupRequestEvent.class));
	}

	@Test
	@DisplayName("일반 회원가입 시 secret이 틀리면 MEMBER_SECRET_INVALID 예외가 발생한다.")
	void normalSignUp_withInvalidSecret_throwsException() {
		// given
		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1990, 1, 1))
			.secret("잘못된비밀")
			.fcmToken("fcm-token-123")
			.build();

		// when & then
		assertThatThrownBy(() -> memberService.normalSignUp(request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException) ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_SECRET_INVALID);
			});
	}

	@Test
	@DisplayName("일반 회원가입 시 이메일이 중복되면 MEMBER_EMAIL_DUPLICATE 예외가 발생한다.")
	void normalSignUp_withDuplicateEmail_throwsException() {
		// given
		memberRepository.save(Member.createKakao("기존회원", "hong@test.com", LocalDate.of(1990, 1, 1), 1001L));

		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1995, 5, 5))
			.secret("오렌지")
			.fcmToken("fcm-token-123")
			.build();

		// when & then
		assertThatThrownBy(() -> memberService.normalSignUp(request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException) ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_EMAIL_DUPLICATE);
			});
	}

	@Test
	@DisplayName("일반 회원가입 시 생년월일이 중복되면 MEMBER_DUPLICATE 예외가 발생한다.")
	void normalSignUp_withDuplicateBirthday_throwsException() {
		// given
		memberRepository.save(Member.createKakao("기존회원", "existing@test.com", LocalDate.of(1990, 1, 1), 1001L));

		MemberNormalSignUpRequest request = MemberNormalSignUpRequest.builder()
			.name("홍길동")
			.email("hong@test.com")
			.password("password123")
			.birthday(LocalDate.of(1990, 1, 1))
			.secret("오렌지")
			.fcmToken("fcm-token-123")
			.build();

		// when & then
		assertThatThrownBy(() -> memberService.normalSignUp(request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException) ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_DUPLICATE);
			});
	}

	@Test
	@DisplayName("회원가입 승인 시 회원 상태가 NORMAL로 변경되고 커뮤니티에 가입된다.")
	void signUpResult_accept_success() {
		// given
		Member member = memberRepository.save(
			Member.createNormal("홍길동", "encodedPassword", "hong@test.com", LocalDate.of(1990, 1, 1)));
		String token = "valid-token-123";

		org.mockito.Mockito.when(signupPendingRepository.verifyToken(member.getId(), token))
			.thenReturn(true);
		org.mockito.Mockito.when(signupPendingRepository.findFcmTokenByMemberId(member.getId()))
			.thenReturn(java.util.Optional.of("fcm-token-123"));

		MemberSignUpResultRequest request = new MemberSignUpResultRequest(member.getId(), token, "accept");

		// when
		memberService.signUpResult(request);

		// then
		Member updated = memberRepository.findByIdAndDeletedAtIsNull(member.getId()).orElseThrow();
		assertThat(updated.getType()).isEqualTo(MemberType.NORMAL);

		// Associate가 생성되었는지 확인
		assertThat(associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())).isPresent();

		// Redis에서 삭제되었는지 확인
		verify(signupPendingRepository, times(1)).deleteByMemberId(member.getId());

		// FCM 이벤트가 발행되었는지 확인 (AssociateFCM + SignupResultFCM)
		verify(fcmEventPublisher, times(2)).publishNotification(any());
	}

	@Test
	@DisplayName("회원가입 거절 시 회원 상태가 REJECT로 변경된다.")
	void signUpResult_reject_success() {
		// given
		Member member = memberRepository.save(
			Member.createNormal("홍길동", "encodedPassword", "hong@test.com", LocalDate.of(1990, 1, 1)));
		String token = "valid-token-123";

		org.mockito.Mockito.when(signupPendingRepository.verifyToken(member.getId(), token))
			.thenReturn(true);
		org.mockito.Mockito.when(signupPendingRepository.findFcmTokenByMemberId(member.getId()))
			.thenReturn(java.util.Optional.of("fcm-token-123"));

		MemberSignUpResultRequest request = new MemberSignUpResultRequest(member.getId(), token, "reject");

		// when
		memberService.signUpResult(request);

		// then
		Member updated = memberRepository.findByIdAndDeletedAtIsNull(member.getId()).orElseThrow();
		assertThat(updated.getType()).isEqualTo(MemberType.REJECT);

		// Associate가 생성되지 않았는지 확인
		assertThat(associateRepository.findByMemberIdAndDeletedAtIsNull(member.getId())).isEmpty();

		// Redis에서 삭제되었는지 확인
		verify(signupPendingRepository, times(1)).deleteByMemberId(member.getId());

		// FCM 이벤트가 발행되었는지 확인 (SignupResultFCM만)
		verify(fcmEventPublisher, times(1)).publishNotification(any(SignupResultFCM.class));
	}

	@Test
	@DisplayName("회원가입 결과 처리 시 토큰이 유효하지 않으면 SIGNUP_TOKEN_INVALID 예외가 발생한다.")
	void signUpResult_withInvalidToken_throwsException() {
		// given
		MemberSignUpResultRequest request = new MemberSignUpResultRequest(9999L, "invalid-token", "accept");

		org.mockito.Mockito.when(signupPendingRepository.verifyToken(9999L, "invalid-token"))
			.thenReturn(false);

		// when & then
		assertThatThrownBy(() -> memberService.signUpResult(request))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException) ex;
				assertThat(me.getErrorCode()).isEqualTo(SIGNUP_TOKEN_INVALID);
			});
	}
}
