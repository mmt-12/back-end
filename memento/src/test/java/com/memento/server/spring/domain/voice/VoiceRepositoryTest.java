package com.memento.server.spring.domain.voice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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
import com.memento.server.spring.api.service.IntegrationsTestSupport;

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
		Associate associate = createAndSaveAssociate();
		Voice voice = Voice.createPermanent("test voice", "https://example.com/test.wav", associate);
		Voice savedVoice = voiceRepository.save(voice);

	    // when
		Optional<Voice> foundVoice = voiceRepository.findByIdAndDeletedAtIsNull(savedVoice.getId());

	    // then
		assertThat(foundVoice).isPresent();
		assertThat(foundVoice.get().getName()).isEqualTo("test voice");
		assertThat(foundVoice.get().getUrl()).isEqualTo("https://example.com/test.wav");
		assertThat(foundVoice.get().isPermanent()).isTrue();
	}
	
	@Test
	@DisplayName("존재하지 않는 id로 조회시 빈 Optional을 반환한다.")
	void findByIdAndDeletedAtIsNullWithNonExistentId() {
	    // given
		Long nonExistentId = 999L;

	    // when
		Optional<Voice> foundVoice = voiceRepository.findByIdAndDeletedAtIsNull(nonExistentId);

	    // then
		assertThat(foundVoice).isEmpty();
	}

	@Test
	@DisplayName("커뮤니티 ID로 permanent voice 목록을 조회한다.")
	void findVoicesByCommunityWithCursor() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Voice voice1 = Voice.createPermanent("voice1", "url1", associate);
		Voice voice2 = Voice.createPermanent("voice2", "url2", associate);
		Voice voice3 = Voice.createPermanent("voice3", "url3", associate);
		
		voiceRepository.saveAll(List.of(voice1, voice2, voice3));

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(3);
		assertThat(voices.get(0).name()).isEqualTo("voice3");
		assertThat(voices.get(1).name()).isEqualTo("voice2");
		assertThat(voices.get(2).name()).isEqualTo("voice1");
	}

	@Test
	@DisplayName("커서를 사용한 페이징이 올바르게 동작한다.")
	void findVoicesByCommunityWithCursorPaging() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Voice voice1 = Voice.createPermanent("voice1", "url1", associate);
		Voice voice2 = Voice.createPermanent("voice2", "url2", associate);
		Voice voice3 = Voice.createPermanent("voice3", "url3", associate);

		voiceRepository.save(voice1);
		voiceRepository.save(voice2);
		Voice savedVoice3 = voiceRepository.save(voice3);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, savedVoice3.getId(), 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(2);
		assertThat(voices.get(0).name()).isEqualTo("voice2");
		assertThat(voices.get(1).name()).isEqualTo("voice1");
	}

	@Test
	@DisplayName("키워드로 voice 이름을 검색한다.")
	void findVoicesByCommunityWithKeyword() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Voice voice1 = Voice.createPermanent("hello world", "url1", associate);
		Voice voice2 = Voice.createPermanent("goodbye", "url2", associate);
		Voice voice3 = Voice.createPermanent("Hello Universe", "url3", associate);
		
		voiceRepository.save(voice1);
		voiceRepository.save(voice2);
		voiceRepository.save(voice3);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, "hello");

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(2);
		assertThat(voices).extracting("name").containsExactly("Hello Universe", "hello world");
	}

	@Test
	@DisplayName("삭제된 voice는 조회되지 않는다.")
	void findVoicesByCommunityWithCursorExcludeDeleted() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Voice voice1 = Voice.createPermanent("voice1", "url1", associate);
		Voice voice2 = Voice.createPermanent("voice2", "url2", associate);
		
		voiceRepository.save(voice1);
		Voice savedVoice2 = voiceRepository.save(voice2);

		savedVoice2.markDeleted();
		em.flush();

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(1);
		assertThat(voices.get(0).name()).isEqualTo("voice1");
	}

	@Test
	@DisplayName("temporary voice는 조회되지 않는다.")
	void findVoicesByCommunityWithCursorExcludeTemporary() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Voice permanentVoice = Voice.createPermanent("permanent", "url1", associate);
		Voice temporaryVoice = Voice.createTemporary("url2", associate);
		
		voiceRepository.save(permanentVoice);
		voiceRepository.save(temporaryVoice);

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		List<VoiceResponse> voices = voiceRepository.findVoicesByCommunityWithCursor(request);

		// then
		assertThat(voices).hasSize(1);
		assertThat(voices.get(0).name()).isEqualTo("permanent");
	}

	private Associate createAndSaveAssociate() {
		Member member = Member.create("김싸피", "test@example.com", null, 12345L);
		Member savedMember = memberRepository.save(member);

		Community community = Community.create("테스트 커뮤니티", savedMember);
		Community savedCommunity = communityRepository.save(community);

		Associate associate = Associate.create("닉네임", savedMember, savedCommunity);
		return associateRepository.save(associate);
	}
}
