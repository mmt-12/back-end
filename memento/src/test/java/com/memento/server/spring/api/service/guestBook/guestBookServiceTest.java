package com.memento.server.spring.api.service.guestBook;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.guestBook.dto.SearchGuestBookResponse;
import com.memento.server.api.service.guestBook.GuestBookService;
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

@SpringBootTest
@Transactional
public class guestBookServiceTest {

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

	@AfterEach
	void afterEach() {
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
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		GuestBook guestBook = GuestBook.builder()
			.type(GuestBookType.TEXT)
			.content("test")
			.associate(associate)
			.build();
		guestBookRepository.save(guestBook);

		Pageable pageable = PageRequest.of(0, 10);

		//when
		SearchGuestBookResponse response = guestBookService.search(community.getId(), associate.getId(), pageable, null);

		//then
		assertThat(response.guestBooks().getFirst().getContent()).isEqualTo("test");
		assertThat(response.guestBooks().getFirst().getType()).isEqualTo(GuestBookType.TEXT);
	}

	@Test
	@DisplayName("텍스트 방명록 생성")
	void createTextTest() {
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		String content = "test";

		// when
		guestBookService.create(community.getId(), associate.getId(), GuestBookType.TEXT, null, content);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(content);
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.TEXT);
	}

	@Test
	@DisplayName("이모지 방명록 생성")
	void createEmojiTest(){
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		Emoji emoji = Emoji.builder()
			.url("www.test.com")
			.associate(associate)
			.name("test")
			.build();
		emojiRepository.save(emoji);

		// when
		guestBookService.create(community.getId(), associate.getId(), GuestBookType.EMOJI, emoji.getId(), null);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(emoji.getUrl());
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.EMOJI);
	}

	@Test
	@DisplayName("보이스 방명록 생성")
	void createVoiceTest() {
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		Voice voice = Voice.builder()
			.associate(associate)
			.url("www.test.com")
			.name("test")
			.temporary(false)
			.build();
		voiceRepository.save(voice);

		// when
		guestBookService.create(community.getId(), associate.getId(), GuestBookType.VOICE, voice.getId(), null);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);

		assertThat(guestBook.getContent()).isEqualTo(voice.getUrl());
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.VOICE);
	}

	@Test
	@DisplayName("일회용 보이스 방명록 생성")
	void createBubbleTest() throws IOException {
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		ClassPathResource resource = new ClassPathResource("static/test-voices/ooh.mp3");
		MockMultipartFile file = new MockMultipartFile(
			"voice",
			resource.getFilename(),
			"audio/mpeg",
			resource.getInputStream()
		);

		// when
		guestBookService.createBubble(community.getId(), associate.getId(), file);

		// then
		GuestBook guestBook = guestBookRepository.findAll().get(0);
		assertThat(guestBook.getType()).isEqualTo(GuestBookType.VOICE);
	}

	@Test
	@DisplayName("방명록 삭제")
	void deleteTest() {
		// given
		Member member = Member.builder()
			.name("example")
			.email("example@example.com")
			.kakaoId(123L)
			.birthday(LocalDate.of(1999, 1, 1))
			.build();
		memberRepository.save(member);

		Community community = Community.builder()
			.member(member)
			.name("example")
			.build();
		communityRepository.save(community);

		Associate associate = Associate.builder()
			.community(community)
			.member(member)
			.nickname("example")
			.build();
		associateRepository.save(associate);

		// when

		// then
	}
}
