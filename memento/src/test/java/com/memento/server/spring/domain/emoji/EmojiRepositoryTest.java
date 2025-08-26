package com.memento.server.spring.domain.emoji;

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
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

import jakarta.persistence.EntityManager;

@Transactional
public class EmojiRepositoryTest extends IntegrationsTestSupport {

	@Autowired
	private EmojiRepository emojiRepository;
	
	@Autowired
	private AssociateRepository associateRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("id에 해당하는 emoji를 조회한다.")
	void findByIdAndDeletedAtIsNull() {
	    // given
		Associate associate = createAndSaveAssociate();
		Emoji emoji = Emoji.create("test emoji", "https://example.com/test.png", associate);
		Emoji savedEmoji = emojiRepository.save(emoji);

	    // when
		Optional<Emoji> foundEmoji = emojiRepository.findByIdAndDeletedAtIsNull(savedEmoji.getId());

	    // then
		assertThat(foundEmoji).isPresent();
		assertThat(foundEmoji.get().getName()).isEqualTo("test emoji");
		assertThat(foundEmoji.get().getUrl()).isEqualTo("https://example.com/test.png");
	}
	
	@Test
	@DisplayName("존재하지 않는 id로 조회시 빈 Optional을 반환한다.")
	void findByIdAndDeletedAtIsNullWithNonExistentId() {
	    // given
		Long nonExistentId = 999L;

	    // when
		Optional<Emoji> foundEmoji = emojiRepository.findByIdAndDeletedAtIsNull(nonExistentId);

	    // then
		assertThat(foundEmoji).isEmpty();
	}

	@Test
	@DisplayName("커뮤니티 ID로 emoji 목록을 조회한다.")
	void findEmojiByCommunityWithCursor() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Emoji emoji1 = Emoji.create("emoji1", "url1", associate);
		Emoji emoji2 = Emoji.create("emoji2", "url2", associate);
		Emoji emoji3 = Emoji.create("emoji3", "url3", associate);
		
		emojiRepository.saveAll(List.of(emoji1, emoji2, emoji3));

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(3);
		assertThat(emojis.get(0).name()).isEqualTo("emoji3");
		assertThat(emojis.get(1).name()).isEqualTo("emoji2");
		assertThat(emojis.get(2).name()).isEqualTo("emoji1");
	}

	@Test
	@DisplayName("커서를 사용한 페이징이 올바르게 동작한다.")
	void findEmojiByCommunityWithCursorPaging() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Emoji emoji1 = Emoji.create("emoji1", "url1", associate);
		Emoji emoji2 = Emoji.create("emoji2", "url2", associate);
		Emoji emoji3 = Emoji.create("emoji3", "url3", associate);

		emojiRepository.save(emoji1);
		emojiRepository.save(emoji2);
		Emoji savedEmoji3 = emojiRepository.save(emoji3);

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, savedEmoji3.getId(), 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(2);
		assertThat(emojis.get(0).name()).isEqualTo("emoji2");
		assertThat(emojis.get(1).name()).isEqualTo("emoji1");
	}

	@Test
	@DisplayName("키워드로 emoji 이름을 검색한다.")
	void findEmojiByCommunityWithKeyword() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Emoji emoji1 = Emoji.create("hello world", "url1", associate);
		Emoji emoji2 = Emoji.create("goodbye", "url2", associate);
		Emoji emoji3 = Emoji.create("Hello Universe", "url3", associate);
		
		emojiRepository.save(emoji1);
		emojiRepository.save(emoji2);
		emojiRepository.save(emoji3);

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, "hello");

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(2);
		assertThat(emojis).extracting("name").containsExactly("Hello Universe", "hello world");
	}

	@Test
	@DisplayName("삭제된 emoji는 조회되지 않는다.")
	void findEmojiByCommunityWithCursorExcludeDeleted() {
		// given
		Associate associate = createAndSaveAssociate();
		Long communityId = associate.getCommunity().getId();
		
		Emoji emoji1 = Emoji.create("emoji1", "url1", associate);
		Emoji emoji2 = Emoji.create("emoji2", "url2", associate);
		
		emojiRepository.save(emoji1);
		Emoji savedEmoji2 = emojiRepository.save(emoji2);

		savedEmoji2.markDeleted();
		em.flush();

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(1);
		assertThat(emojis.get(0).name()).isEqualTo("emoji1");
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
