package com.memento.server.spring.api.service.emoji;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.UNAUTHORIZED_EMOJI_ACCESS;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NOT_FOUND;
import static com.memento.server.config.MinioProperties.FileType.EMOJI;
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
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.emoji.EmojiService;
import com.memento.server.api.service.emoji.dto.request.EmojiCreateServiceRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.request.EmojiRemoveRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiListResponse;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class EmojiServiceTest extends IntegrationsTestSupport {

	@Autowired
	private EmojiRepository emojiRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private EmojiService emojiService;

	@AfterEach
	public void tearDown() {
		memberRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		emojiRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("emoji를 생성한다.")
	void createEmoji() {
		// given
		Associate associate = createAndSaveAssociate();
		MultipartFile file = new MockMultipartFile("emoji", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";

		EmojiCreateServiceRequest request = EmojiCreateServiceRequest.builder()
			.name("test emoji")
			.associateId(associate.getId())
			.emoji(file)
			.build();

		given(minioService.createFile(file, EMOJI))
			.willReturn(url);

		// when
		emojiService.createEmoji(request);

		// then
		Optional<Emoji> savedEmoji = emojiRepository.findAll().stream().findFirst();
		assertThat(savedEmoji).isPresent();
		assertThat(savedEmoji.get().getName()).isEqualTo("test emoji");
		assertThat(savedEmoji.get().getUrl()).isEqualTo(url);
		assertThat(savedEmoji.get().getAssociate().getId()).isEqualTo(associate.getId());
	}

	@Test
	@DisplayName("존재하지 않는 associate가 emoji를 생성하면 예외가 발생한다.")
	void createEmojiWithNoAssociate() {
		// given
		MultipartFile file = new MockMultipartFile("emoji", "test.png", "image/png", "test".getBytes());

		EmojiCreateServiceRequest request = EmojiCreateServiceRequest.builder()
			.name("test emoji")
			.associateId(0L)
			.emoji(file)
			.build();

		// when && then
		assertThatThrownBy(() -> emojiService.createEmoji(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("커뮤니티의 emoji 목록을 조회한다.")
	void getEmojis() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		Emoji emoji1 = Emoji.create("emoji1", "url1", associate);
		Emoji emoji2 = Emoji.create("emoji2", "url2", associate);
		Emoji emoji3 = Emoji.create("emoji3", "url3", associate);
		emojiRepository.saveAll(List.of(emoji1, emoji2, emoji3));

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		EmojiListResponse response = emojiService.getEmoji(request);

		// then
		assertThat(response.emoji()).hasSize(3);
		assertThat(response.pageInfo().hasNext()).isFalse();
		assertThat(response.pageInfo().nextCursor()).isNull();
	}

	@Test
	@DisplayName("페이지 사이즈보다 많은 emoji가 있을 때 hasNext가 true이다.")
	void getEmojisWithPagination() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		Emoji emoji1 = Emoji.create("emoji1", "url1", associate);
		Emoji emoji2 = Emoji.create("emoji2", "url2", associate);
		Emoji emoji3 = Emoji.create("emoji3", "url3", associate);
		emojiRepository.saveAll(List.of(emoji1, emoji2, emoji3));

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 2, null);

		// when
		EmojiListResponse response = emojiService.getEmoji(request);

		// then
		assertThat(response.emoji()).hasSize(2);
		assertThat(response.pageInfo().hasNext()).isTrue();
		assertThat(response.pageInfo().nextCursor()).isNotNull();
	}

	@Test
	@DisplayName("빈 커뮤니티에서 emoji 목록을 조회하면 빈 리스트를 반환한다.")
	void getEmojisFromEmptyCommunity() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		EmojiListResponse response = emojiService.getEmoji(request);

		// then
		assertThat(response.emoji()).isEmpty();
		assertThat(response.pageInfo().hasNext()).isFalse();
		assertThat(response.pageInfo().nextCursor()).isNull();
	}

	@Test
	@DisplayName("이모지를 삭제한다.")
	void removeEmoji() {
		// given
		Associate associate = createAndSaveAssociate();
		Emoji emoji = Emoji.create("emoji", "url", associate);
		emojiRepository.save(emoji);

		EmojiRemoveRequest request = EmojiRemoveRequest.of(associate.getId(), emoji.getId());

		doNothing().when(minioService).removeFile(emoji.getUrl());

		// when
		emojiService.removeEmoji(request);

		// then
		Optional<Emoji> deletedEmoji = emojiRepository.findById(emoji.getId());
		assertThat(deletedEmoji).isPresent();
		assertThat(deletedEmoji.get().getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("존재하지 않는 이모지를 삭제하면 예외가 발생한다.")
	void removeEmojiNotFound() {
		// given
		Associate associate = createAndSaveAssociate();
		EmojiRemoveRequest request = EmojiRemoveRequest.of(associate.getId(), 999L);

		// when & then
		assertThatThrownBy(() ->
			emojiService.removeEmoji(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(EMOJI_NOT_FOUND);
	}

	@Test
	@DisplayName("권한이 없는 associate가 이모지를 삭제하면 예외가 발생한다.")
	void removeEmojiUnauthorized() {
		// given
		Associate emojiOwner = createAndSaveAssociate();
		Associate otherAssociate = createAndSaveAssociate();
		
		Emoji emoji = Emoji.create("emoji", "url", emojiOwner);
		emojiRepository.save(emoji);

		EmojiRemoveRequest request = EmojiRemoveRequest.of(otherAssociate.getId(), emoji.getId());

		// when & then
		assertThatThrownBy(() -> emojiService.removeEmoji(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(UNAUTHORIZED_EMOJI_ACCESS);
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
