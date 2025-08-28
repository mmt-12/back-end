package com.memento.server.spring.domain.emoji;

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
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.api.service.emoji.dto.request.EmojiListQueryRequest;
import com.memento.server.api.service.emoji.dto.response.EmojiResponse;
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.member.MemberFixtures;
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
		Fixtures fixtures = createFixtures();
		Emoji emoji = EmojiFixtures.emoji(fixtures.associate);
		Emoji savedEmoji = emojiRepository.save(emoji);

	    // when
		Optional<Emoji> foundEmoji = emojiRepository.findByIdAndDeletedAtIsNull(savedEmoji.getId());

	    // then
		assertThat(foundEmoji).isPresent();
		assertThat(foundEmoji.get()).isEqualTo(savedEmoji);
	}
	
	@Test
	@DisplayName("존재하지 않는 id로 조회시 빈 Optional을 반환한다.")
	void findByIdAndDeletedAtIsNullWithNonExistentId() {
	    // given
		Long nonExistentId = 999L;

	    // when
		Optional<Emoji> foundEmoji = emojiRepository.findByIdAndDeletedAtIsNull(nonExistentId);

	    // then
		assertThat(foundEmoji).isNotPresent();
	}

	@Test
	@DisplayName("커뮤니티 ID로 emoji 목록을 조회한다.")
	void findEmojiByCommunityWithCursor() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community.getId();

		Emoji emoji1 = EmojiFixtures.emoji(fixtures.associate);
		Emoji emoji2 = EmojiFixtures.emoji(fixtures.associate);
		Emoji emoji3 = EmojiFixtures.emoji(fixtures.associate);
		
		Emoji savedEmoji1 = emojiRepository.save(emoji1);
		Emoji savedEmoji2 = emojiRepository.save(emoji2);
		Emoji savedEmoji3 = emojiRepository.save(emoji3);

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(3);
		assertThat(emojis.get(0).id()).isEqualTo(savedEmoji3.getId());
		assertThat(emojis.get(1).id()).isEqualTo(savedEmoji2.getId());
		assertThat(emojis.get(2).id()).isEqualTo(savedEmoji1.getId());
	}

	@Test
	@DisplayName("커서를 사용한 페이징이 올바르게 동작한다.")
	void findEmojiByCommunityWithCursorPaging() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community.getId();

		Emoji emoji1 = EmojiFixtures.emoji(fixtures.associate);
		Emoji emoji2 = EmojiFixtures.emoji(fixtures.associate);
		Emoji emoji3 = EmojiFixtures.emoji(fixtures.associate);

		Emoji savedEmoji1 = emojiRepository.save(emoji1);
		Emoji savedEmoji2 = emojiRepository.save(emoji2);
		Emoji savedEmoji3 = emojiRepository.save(emoji3);

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, savedEmoji3.getId(), 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(2);
		assertThat(emojis.get(0).id()).isEqualTo(savedEmoji2.getId());
		assertThat(emojis.get(1).id()).isEqualTo(savedEmoji1.getId());
	}

	@Test
	@DisplayName("키워드로 emoji 이름을 검색한다.")
	void findEmojiByCommunityWithKeyword() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community.getId();
		final String KEYWORD = "hello";

		Emoji emoji1 = EmojiFixtures.emoji(KEYWORD + " world", fixtures.associate);
		Emoji emoji2 = EmojiFixtures.emoji("goodbye", fixtures.associate);
		Emoji emoji3 = EmojiFixtures.emoji(KEYWORD.toUpperCase() + " Universe", fixtures.associate);

		Emoji savedEmoji1 = emojiRepository.save(emoji1);
		Emoji savedEmoji2 = emojiRepository.save(emoji2);
		Emoji savedEmoji3 = emojiRepository.save(emoji3);

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, "hello");

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(2);
		assertThat(emojis).extracting("name").containsExactly(savedEmoji3.getName(), savedEmoji1.getName());
	}

	@Test
	@DisplayName("삭제된 emoji는 조회되지 않는다.")
	void findEmojiByCommunityWithCursorExcludeDeleted() {
		// given
		Fixtures fixtures = createFixtures();
		Long communityId = fixtures.community.getId();

		Emoji emoji1 = EmojiFixtures.emoji(fixtures.associate);
		Emoji emoji2 = EmojiFixtures.emoji(fixtures.associate);

		Emoji savedEmoji1 = emojiRepository.save(emoji1);
		Emoji savedEmoji2 = emojiRepository.save(emoji2);

		savedEmoji2.markDeleted();
		em.flush();

		EmojiListQueryRequest request = EmojiListQueryRequest.of(communityId, null, 10, null);

		// when
		List<EmojiResponse> emojis = emojiRepository.findEmojiByCommunityWithCursor(request);

		// then
		assertThat(emojis).hasSize(1);
		assertThat(emojis.get(0).id()).isEqualTo(savedEmoji1.getId());
	}

	@Test
	@DisplayName("이름이 존재하는 경우 true를 반환한다.")
	void existsByNameAndDeletedAtIsNullReturnsTrue() {
		// given
		Fixtures fixtures = createFixtures();
		String emojiName = "testEmoji";
		Emoji emoji = EmojiFixtures.emoji(emojiName, fixtures.associate);
		emojiRepository.save(emoji);

		// when
		boolean exists = emojiRepository.existsByNameAndDeletedAtIsNull(emojiName);

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("이름이 존재하지 않는 경우 false를 반환한다.")
	void existsByNameAndDeletedAtIsNullReturnsFalse() {
		// given
		String nonExistentName = "nonExistentEmoji";

		// when
		boolean exists = emojiRepository.existsByNameAndDeletedAtIsNull(nonExistentName);

		// then
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("삭제된 emoji 이름은 존재하지 않는 것으로 처리된다.")
	void existsByNameAndDeletedAtIsNullWithDeletedEmoji() {
		// given
		Fixtures fixtures = createFixtures();
		String emojiName = "deletedEmoji";
		Emoji emoji = EmojiFixtures.emoji(emojiName, fixtures.associate);
		Emoji savedEmoji = emojiRepository.save(emoji);
		
		savedEmoji.markDeleted();
		em.flush();

		// when
		boolean exists = emojiRepository.existsByNameAndDeletedAtIsNull(emojiName);

		// then
		assertThat(exists).isFalse();
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
