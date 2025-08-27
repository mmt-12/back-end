package com.memento.server.spring.domain.voice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.response.VoiceResponse;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;
import com.memento.server.voice.VoiceFixtures;

import jakarta.persistence.EntityManager;

@Transactional
public class VoiceRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private VoiceRepository voiceRepository;
	
	@Autowired
	private AssociateRepository associateRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("id에 해당하는 voice를 조회한다.")
	void findByIdAndDeletedAtIsNull() {
	    // given
		Fixtures fixtures = createFixtures();
		Voice voice = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice savedVoice = voiceRepository.save(voice);

	    // when
		Optional<Voice> foundVoice = voiceRepository.findByIdAndDeletedAtIsNull(savedVoice.getId());

	    // then
		assertThat(foundVoice).isPresent();
		assertThat(foundVoice.get()).isEqualTo(savedVoice);
	}
	
	@Test
	@DisplayName("존재하지 않는 id로 조회시 빈 Optional을 반환한다.")
	void findByIdAndDeletedAtIsNullWithNonExistentId() {
	    // given
		Long nonExistentId = 999L;

	    // when
		Optional<Voice> foundVoice = voiceRepository.findByIdAndDeletedAtIsNull(nonExistentId);

	    // then
		assertThat(foundVoice).isNotPresent();
	}

	@Test
	@DisplayName("커뮤니티 ID로 permanent voice 목록을 조회한다.")
	void findVoicesByCommunityWithCursor() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice voice1 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice3 = VoiceFixtures.permanentVoice(fixtures.associate);

		Voice savedVoice1 = voiceRepository.save(voice1);
		Voice savedVoice2 = voiceRepository.save(voice2);
		Voice savedVoice3 = voiceRepository.save(voice3);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(3);
		assertThat(voices.get(0).id()).isEqualTo(savedVoice3.getId());
		assertThat(voices.get(1).id()).isEqualTo(savedVoice2.getId());
		assertThat(voices.get(2).id()).isEqualTo(savedVoice1.getId());
	}

	@Test
	@DisplayName("커서를 사용한 페이징이 올바르게 동작한다.")
	void findVoicesByCommunityWithCursorPaging() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice voice1 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice3 = VoiceFixtures.permanentVoice(fixtures.associate);

		Voice savedVoice1 = voiceRepository.save(voice1);
		Voice savedVoice2 = voiceRepository.save(voice2);
		Voice savedVoice3 = voiceRepository.save(voice3);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, savedVoice3.getId(), 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(2);
		assertThat(voices.get(0).id()).isEqualTo(savedVoice2.getId());
		assertThat(voices.get(1).id()).isEqualTo(savedVoice1.getId());
	}

	@Test
	@DisplayName("키워드로 voice 이름을 검색한다.")
	void findVoicesByCommunityWithKeyword() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();
		final String KEYWORD = "hello";

		Voice voice1 = VoiceFixtures.permanentVoice(KEYWORD + " world", "url1", fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice("goodbye", "url2", fixtures.associate);
		Voice voice3 = VoiceFixtures.permanentVoice(KEYWORD.toUpperCase() + " Universe", "url3", fixtures.associate);
		
		Voice savedVoice1 = voiceRepository.save(voice1);
		Voice savedVoice2 = voiceRepository.save(voice2);
		Voice savedVoice3 = voiceRepository.save(voice3);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, KEYWORD);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(2);
		assertThat(voices).extracting("name").containsExactly(savedVoice3.getName(), savedVoice1.getName());
	}

	@Test
	@DisplayName("삭제된 voice는 조회되지 않는다.")
	void findVoicesByCommunityWithCursorExcludeDeleted() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice voice1 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice(fixtures.associate);

		Voice savedVoice1 = voiceRepository.save(voice1);
		Voice savedVoice2 = voiceRepository.save(voice2);

		savedVoice2.markDeleted();
		em.flush();

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(1);
		assertThat(voices.get(0).id()).isEqualTo(savedVoice1.getId());
	}

	@Test
	@DisplayName("temporary voice는 조회되지 않는다.")
	void findVoicesByCommunityWithCursorExcludeTemporary() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice permanentVoice = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice temporaryVoice = VoiceFixtures.temporaryVoice(fixtures.associate);
		
		Voice savedPermanentVoice = voiceRepository.save(permanentVoice);
		Voice savedTemporaryVoice = voiceRepository.save(temporaryVoice);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(1);
		assertThat(voices.get(0).id()).isEqualTo(savedPermanentVoice.getId());
	}

	@Test
	@DisplayName("URL로 voice를 조회한다.")
	void findByUrlAndDeletedAtIsNull() {
		// given
		Fixtures fixtures = createFixtures();
		String voiceUrl = "https://example.com/voice.wav";
		Voice voice = VoiceFixtures.permanentVoice(voiceUrl, fixtures.associate);
		Voice savedVoice = voiceRepository.save(voice);

		// when
		Optional<Voice> foundVoice = voiceRepository.findByUrlAndDeletedAtIsNull(voiceUrl);

		// then
		assertThat(foundVoice).isPresent();
		assertThat(foundVoice.get()).isEqualTo(savedVoice);
		assertThat(foundVoice.get().getUrl()).isEqualTo(voiceUrl);
		assertThat(foundVoice.get().getAssociate().getId()).isEqualTo(fixtures.associate.getId());
	}

	@Test
	@DisplayName("존재하지 않는 URL로 조회시 빈 Optional을 반환한다.")
	void findByUrlAndDeletedAtIsNullWithNonExistentUrl() {
		// given
		String nonExistentUrl = "https://example.com/nonexistent.wav";

		// when
		Optional<Voice> foundVoice = voiceRepository.findByUrlAndDeletedAtIsNull(nonExistentUrl);

		// then
		assertThat(foundVoice).isNotPresent();
	}

	@Test
	@DisplayName("삭제된 voice는 URL로 조회되지 않는다.")
	void findByUrlAndDeletedAtIsNullWithDeletedVoice() {
		// given
		Fixtures fixtures = createFixtures();
		String voiceUrl = "https://example.com/deleted-voice.wav";
		Voice voice = VoiceFixtures.permanentVoice(voiceUrl, fixtures.associate);
		Voice savedVoice = voiceRepository.save(voice);

		savedVoice.markDeleted();
		em.flush();

		// when
		Optional<Voice> foundVoice = voiceRepository.findByUrlAndDeletedAtIsNull(voiceUrl);

		// then
		assertThat(foundVoice).isNotPresent();
	}

	@Test
	@DisplayName("temporary voice도 URL로 조회할 수 있다.")
	void findByUrlAndDeletedAtIsNullWithTemporaryVoice() {
		// given
		Fixtures fixtures = createFixtures();
		String voiceUrl = "https://example.com/temporary-voice.wav";
		Voice voice = VoiceFixtures.temporaryVoice(voiceUrl, fixtures.associate);
		Voice savedVoice = voiceRepository.save(voice);

		// when
		Optional<Voice> foundVoice = voiceRepository.findByUrlAndDeletedAtIsNull(voiceUrl);

		// then
		assertThat(foundVoice).isPresent();
		assertThat(foundVoice.get()).isEqualTo(savedVoice);
		assertThat(foundVoice.get().getUrl()).isEqualTo(voiceUrl);
		assertThat(foundVoice.get().isPermanent()).isFalse();
	}

	private record Fixtures(
		Member member,
		Community community,
		Associate associate
	) {

	}

	private Fixtures createFixtures() {
		Member member = MemberFixtures.member();
		Community community = CommunityFixtures.community(member);
		Associate associate = AssociateFixtures.associate(member, community);

		Member savedMember = memberRepository.save(member);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate = associateRepository.save(associate);

		return new Fixtures(savedMember, savedCommunity, savedAssociate);
	}
}
