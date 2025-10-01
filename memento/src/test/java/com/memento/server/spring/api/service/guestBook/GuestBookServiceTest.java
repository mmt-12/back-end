package com.memento.server.spring.api.service.guestBook;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.api.service.guestBook.GuestBookService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.guestBook.GuestBook;
import com.memento.server.domain.guestBook.GuestBookRepository;
import com.memento.server.domain.guestBook.GuestBookType;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;
import com.memento.server.voice.VoiceFixtures;

public class GuestBookServiceTest extends IntegrationsTestSupport {

	@Autowired
	private GuestBookService guestBookService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private GuestBookRepository guestBookRepository;

	@Autowired
	private EmojiRepository emojiRepository;

	@Autowired
	private VoiceRepository voiceRepository;

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@BeforeEach
	void BeforeEach() {
		voiceRepository.deleteAll();
		emojiRepository.deleteAll();
		guestBookRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("방명록 조회")
	void searchTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		GuestBook guestBook = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test")
			.associate(associate)
			.build();
		guestBookRepository.save(guestBook);

		//when
		SearchGuestBookResponse response = guestBookService.search(community.getId(), associate.getId(), 10, null);

		//then
		assertThat(response.guestBooks().getFirst().getContent()).isEqualTo("test");
		assertThat(response.guestBooks().getFirst().getName()).isNull();
		assertThat(response.guestBooks().getFirst().getType()).isEqualTo(GuestBookType.TEXT);
	}

	@Test
	@DisplayName("방명록 cursor 조회")
	void searchWithCursorTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		GuestBook guestBook1 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test")
			.associate(associate)
			.build();
		guestBookRepository.save(guestBook1);

		GuestBook guestBook2 = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test")
			.associate(associate)
			.build();
		guestBookRepository.save(guestBook2);

		//when
		SearchGuestBookResponse response = guestBookService.search(community.getId(), associate.getId(), 1, null);

		//then
		assertThat(response.guestBooks().size()).isEqualTo(1);
		assertThat(response.nextCursor()).isEqualTo(guestBook1.getId());
		assertThat(response.hasNext()).isTrue();
	}

	@Test
	@DisplayName("텍스트 방명록 생성")
	void createTextTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		String content = "test";

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.TEXT, null, content);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(content);
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.TEXT);
	}

	@Test
	@DisplayName("이모지 방명록 생성")
	void createEmojiTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Emoji emoji = EmojiFixtures.emoji();
		emojiRepository.save(emoji);

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.EMOJI, emoji.getId(), null);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(emoji.getUrl());
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.EMOJI);
	}

	@Test
	@DisplayName("보이스 방명록 생성")
	void createVoiceTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Voice voice = VoiceFixtures.permanentVoice("test", "https://example.com/test.mp3", associate);
		voiceRepository.save(voice);

		// when
		guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.VOICE, voice.getId(), null);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(voice.getUrl());
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.VOICE);
	}

	@Test
	@DisplayName("존재하지 않은 리액션의 방명록은 생성할 수 없다")
	void createWithNotExistedReactionTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		// when & then
		assertThrows(MementoException.class, () -> guestBookService.create(community.getId(), register.getId(), associate.getId(), GuestBookType.VOICE, 1L, null));
	}

	@Test
	@DisplayName("일회용 보이스 방명록 생성")
	void createBubbleTest() throws IOException {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate register = AssociateFixtures.associate(member, community);
		associateRepository.save(register);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		MultipartFile file = new MockMultipartFile("voice", "test.mp3", "audio/mpeg", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.VOICE))
			.willReturn(url);

		// when
		guestBookService.createBubble(community.getId(), register.getId(), associate.getId(), file);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.VOICE);
	}

	@Test
	@DisplayName("방명록 삭제")
	void deleteTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		GuestBook guestBook = GuestBook.builder()
			.associate(associate)
			.content("test")
			.type(GuestBookType.TEXT)
			.build();
		guestBookRepository.save(guestBook);

		// when
		guestBookService.delete(guestBook.getId());

		// then
		assertThat(guestBookRepository.findById(guestBook.getId()).get().getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("존재 하지 않은 방명록은 삭제할 수 없다")
	void deleteWithNotExistenceTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		// when & then
		assertThrows(MementoException.class, () -> guestBookService.delete(1L));
	}
}
