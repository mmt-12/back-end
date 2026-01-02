package com.memento.server.spring.api.service.member;

import static com.memento.server.common.error.ErrorCodes.MEMBER_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_EMAIL_DUPLICATE;
import static com.memento.server.common.error.ErrorCodes.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static reactor.core.publisher.Mono.when;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.controller.member.dto.CommunityListResponse;
import com.memento.server.api.controller.member.dto.MemberSignUpRequest;
import com.memento.server.api.controller.member.dto.MemberSignUpResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.community.AssociateService;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.member.MemberService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;

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

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@AfterEach
	void afterEach() {
		memberRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
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
	@DisplayName("이메일 중복 체크 시 사용 가능한 이메일이면 예외가 발생하지 않는다.")
	void checkDuplicateEmail_withAvailableEmail_success() {
		// given
		String email = "new@test.com";

		// when & then
		assertThatCode(() -> memberService.checkDuplicateEmail(email))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("이메일 중복 체크 시 이미 존재하는 이메일이면 MEMBER_EMAIL_DUPLICATE 예외가 발생한다.")
	void checkDuplicateEmail_withDuplicateEmail_throwsException() {
		// given
		String email = "existing@test.com";
		memberRepository.save(Member.createKakao("홍길동", email, LocalDate.of(1990, 1, 1), 1001L));

		// when & then
		assertThatThrownBy(() -> memberService.checkDuplicateEmail(email))
			.isInstanceOf(MementoException.class)
			.satisfies(ex -> {
				MementoException me = (MementoException) ex;
				assertThat(me.getErrorCode()).isEqualTo(MEMBER_EMAIL_DUPLICATE);
			});
	}
}
