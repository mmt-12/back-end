package com.memento.server.spring.api.service.voice;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.UNAUTHORIZED_VOICE_ACCESS;
import static com.memento.server.common.error.ErrorCodes.VOICE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.VOICE_NAME_DUPLICATE;
import static com.memento.server.config.MinioProperties.FileType.VOICE;
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
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.voice.VoiceService;
import com.memento.server.api.service.voice.dto.request.PermanentVoiceCreateServiceRequest;
import com.memento.server.api.service.voice.dto.request.VoiceListQueryRequest;
import com.memento.server.api.service.voice.dto.request.VoiceRemoveRequest;
import com.memento.server.api.service.voice.dto.response.VoiceListResponse;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.common.fixture.CommonFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
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
	private MinioProperties minioProperties;

	@Autowired
	private VoiceService voiceService;

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
		Fixtures fixtures = createFixtures();
		MultipartFile file = CommonFixtures.voiceFile();
		String url = CommonFixtures.mockUrl(minioProperties, file, VOICE);
		String name = "name";

		PermanentVoiceCreateServiceRequest request = PermanentVoiceCreateServiceRequest.builder()
			.name(name)
			.associateId(fixtures.associate.getId())
			.voice(file)
			.build();

		given(minioService.createFile(file, VOICE))
			.willReturn(url);

		// when
		voiceService.createPermanentVoice(request);

		// then
		Optional<Voice> savedVoice = voiceRepository.findAll().stream().findFirst();
		assertThat(savedVoice).isPresent();
		assertThat(savedVoice.get().getName()).isEqualTo(name);
		assertThat(savedVoice.get().getUrl()).isEqualTo(url);
		assertThat(savedVoice.get().isPermanent()).isTrue();
		assertThat(savedVoice.get().getAssociate().getId()).isEqualTo(fixtures.associate.getId());
	}

	@Test
	@DisplayName("존재하지 않는 associate가 permanent voice를 생성하면 예외가 발생한다.")
	void createPermanentVoiceWithNoAssociate() {
		// given
		MultipartFile file = CommonFixtures.voiceFile();

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
	@DisplayName("중복된 이름으로 permanent voice를 생성하면 예외가 발생한다.")
	void createPermanentVoiceWithDuplicateName() {
		// given
		Fixtures fixtures = createFixtures();
		String duplicateName = "duplicateName";
		MultipartFile file = CommonFixtures.voiceFile();

		Voice existingVoice = VoiceFixtures.permanentVoice(duplicateName, "url1", fixtures.associate);
		voiceRepository.save(existingVoice);

		PermanentVoiceCreateServiceRequest request = PermanentVoiceCreateServiceRequest.builder()
			.name(duplicateName)
			.associateId(fixtures.associate.getId())
			.voice(file)
			.build();

		// when & then
		assertThatThrownBy(() -> voiceService.createPermanentVoice(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(VOICE_NAME_DUPLICATE);
	}

	@Test
	@DisplayName("커뮤니티의 voice 목록을 조회한다.")
	void getVoices() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice voice1 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice3 = VoiceFixtures.permanentVoice(fixtures.associate);
		voiceRepository.saveAll(List.of(voice1, voice2, voice3));

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).hasSize(3);
		assertThat(response.hasNext()).isFalse();
		assertThat(response.nextCursor()).isNull();
	}

	@Test
	@DisplayName("페이지 사이즈보다 많은 voice가 있을 때 hasNext가 true이다.")
	void getVoicesWithPagination() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		Voice voice1 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice2 = VoiceFixtures.permanentVoice(fixtures.associate);
		Voice voice3 = VoiceFixtures.permanentVoice(fixtures.associate);
		voiceRepository.saveAll(List.of(voice1, voice2, voice3));

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 2, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).hasSize(2);
		assertThat(response.hasNext()).isTrue();
		assertThat(response.nextCursor()).isNotNull();
	}

	@Test
	@DisplayName("빈 커뮤니티에서 voice 목록을 조회하면 빈 리스트를 반환한다.")
	void getVoicesFromEmptyCommunity() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community().getId();

		VoiceListQueryRequest request = VoiceListQueryRequest.of(communityId, null, 10, null);

		// when
		VoiceListResponse response = voiceService.getVoices(request);

		// then
		assertThat(response.voices()).isEmpty();
		assertThat(response.hasNext()).isFalse();
		assertThat(response.nextCursor()).isNull();
	}

	@Test
	@DisplayName("보이스를 삭제한다.")
	void removeVoice() {
		// given
		Fixtures fixtures = createFixtures();
		Voice voice = VoiceFixtures.permanentVoice(fixtures.associate);
		voiceRepository.save(voice);

		VoiceRemoveRequest request = VoiceRemoveRequest.of(fixtures.associate.getId(), voice.getId());

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
		Fixtures fixtures = createFixtures();
		VoiceRemoveRequest request = VoiceRemoveRequest.of(fixtures.associate.getId(), 999L);

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
		Fixtures fixtures = createFixtures();
		Associate voiceOwner = fixtures.associate;
		Associate otherAssociate = AssociateFixtures.associate(fixtures.member, fixtures.community);
		
		Voice voice = VoiceFixtures.permanentVoice(voiceOwner);
		voiceRepository.save(voice);

		VoiceRemoveRequest request = VoiceRemoveRequest.of(otherAssociate.getId(), voice.getId());

		// when & then
		assertThatThrownBy(() -> voiceService.removeVoice(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(UNAUTHORIZED_VOICE_ACCESS);
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
