package com.memento.server.spring.api.service.voice;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.UNAUTHORIZED_VOICE_ACCESS;
import static com.memento.server.common.error.ErrorCodes.VOICE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.minio.MinioService;
import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.PermanentVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;
import com.memento.server.voice.VoiceFixtures;

public class VoiceServiceTest extends IntegrationsTestSupport {

	@Autowired
	private VoiceRepository voiceRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private VoiceService voiceService;

	@MockitoBean
	private MinioService minioService;

	@AfterEach
	public void tearDown() {
		memberRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		voiceRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("permanent voice를 생성한다.")
	void createPermanentVoice() {
		// given
		Associate associate = createAndSaveAssociate();
		MultipartFile file = new MockMultipartFile("voice", "test.wav", "audio/wav", "test".getBytes());
		String url = "https://example.com/test.wav";

		PermanentVoiceCreateServiceRequest request = PermanentVoiceCreateServiceRequest.builder()
			.name("test voice")
			.associateId(associate.getId())
			.voice(file)
			.build();

		given(minioService.createFile(file))
			.willReturn(url);

		// when
		voiceService.createPermanentVoice(request);

		// then
		Optional<Voice> savedVoice = voiceRepository.findAll().stream().findFirst();
		assertThat(savedVoice).isPresent();
		assertThat(savedVoice.get().getName()).isEqualTo("test voice");
		assertThat(savedVoice.get().getUrl()).isEqualTo(url);
		assertThat(savedVoice.get().isPermanent()).isTrue();
		assertThat(savedVoice.get().getAssociate().getId()).isEqualTo(associate.getId());
	}

	@Test
	@DisplayName("존재하지 않는 associate가 permanent voice를 생성하면 예외가 발생한다.")
	void createPermanentVoiceWithNoAssociate() {
		// given
		MultipartFile file = new MockMultipartFile("voice", "test.wav", "audio/wav", "test".getBytes());

		PermanentVoiceCreateServiceRequest request = PermanentVoiceCreateServiceRequest.builder()
			.name("test voice")
			.associateId(0L)
			.voice(file)
			.build();

		// when && then
		assertThatThrownBy(() -> voiceService.createPermanentVoice(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("커뮤니티의 voice 목록을 조회한다.")
	void getVoices() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		Voice voice1 = VoiceFixtures.permanentVoice("voice1", "url1", associate);
		Voice voice2 = VoiceFixtures.permanentVoice("voice2", "url2", associate);
		Voice voice3 = VoiceFixtures.permanentVoice("voice2", "url2", associate);
		voiceRepository.saveAll(List.of(voice1, voice2, voice3));

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).hasSize(3);
		assertThat(response.pageInfo().hasNext()).isFalse();
		assertThat(response.pageInfo().nextCursor()).isNull();
	}

	@Test
	@DisplayName("페이지 사이즈보다 많은 voice가 있을 때 hasNext가 true이다.")
	void getVoicesWithPagination() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		Voice voice1 = VoiceFixtures.permanentVoice("voice1", "url1", associate);
		Voice voice2 = VoiceFixtures.permanentVoice("voice2", "url2", associate);
		Voice voice3 = VoiceFixtures.permanentVoice("voice2", "url2", associate);
		voiceRepository.saveAll(List.of(voice1, voice2, voice3));

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 2, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).hasSize(2);
		assertThat(response.pageInfo().hasNext()).isTrue();
		assertThat(response.pageInfo().nextCursor()).isNotNull();
	}

	@Test
	@DisplayName("빈 커뮤니티에서 voice 목록을 조회하면 빈 리스트를 반환한다.")
	void getVoicesFromEmptyCommunity() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).isEmpty();
		assertThat(response.pageInfo().hasNext()).isFalse();
		assertThat(response.pageInfo().nextCursor()).isNull();
	}

	@Test
	@DisplayName("보이스를 삭제한다.")
	void removeVoice() {
		// given
		Associate associate = createAndSaveAssociate();
		Voice voice = VoiceFixtures.permanentVoice("voice", "url", associate);
		voiceRepository.save(voice);

		VoiceRemoveRequest request = VoiceRemoveRequest.of(associate.getId(), voice.getId());

		doNothing().when(minioService).removeFile(voice.getUrl());

		// when
		voiceService.removeVoice(request);

		// then
		Optional<Voice> deletedVoice = voiceRepository.findById(voice.getId());
		assertThat(deletedVoice).isPresent();
		assertThat(deletedVoice.get().getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("존재하지 않는 보이스를 삭제하면 예외가 발생한다.")
	void removeVoiceNotFound() {
		// given
		Associate associate = createAndSaveAssociate();
		VoiceRemoveRequest request = VoiceRemoveRequest.of(associate.getId(), 999L);

		// when & then
		assertThatThrownBy(() ->
			voiceService.removeVoice(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(VOICE_NOT_FOUND);
	}

	@Test
	@DisplayName("권한이 없는 associate가 보이스를 삭제하면 예외가 발생한다.")
	void removeVoiceUnauthorized() {
		// given
		Associate voiceOwner = createAndSaveAssociate();
		Associate otherAssociate = createAndSaveAssociate();
		
		Voice voice = VoiceFixtures.permanentVoice("voice", "url", voiceOwner);
		voiceRepository.save(voice);

		VoiceRemoveRequest request = VoiceRemoveRequest.of(otherAssociate.getId(), voice.getId());

		// when & then
		assertThatThrownBy(() -> voiceService.removeVoice(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(UNAUTHORIZED_VOICE_ACCESS);
	}

	private Associate createAndSaveAssociate() {
		Member member = MemberFixtures.member();
		Member savedMember = memberRepository.save(member);

		Community community = CommunityFixtures.communityWithMember(savedMember);
		Community savedCommunity = communityRepository.save(community);

		Associate associate = AssociateFixtures.associateWithMemberAndCommunity(savedMember, savedCommunity);
		return associateRepository.save(associate);
	}
}
