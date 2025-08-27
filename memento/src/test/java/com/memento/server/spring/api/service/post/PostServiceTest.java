package com.memento.server.spring.api.service.post;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.controller.post.dto.SearchAllPostResponse;
import com.memento.server.api.controller.post.dto.SearchPostResponse;
import com.memento.server.api.service.post.PostService;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.comment.CommentType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.event.Event;
import com.memento.server.domain.event.EventRepository;
import com.memento.server.domain.event.Location;
import com.memento.server.domain.event.Period;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

@SpringBootTest
@Transactional
public class PostServiceTest {

	@Autowired
	private PostService postService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private VoiceRepository voiceRepository;

	@Autowired
	private EmojiRepository emojiRepository;

	@Autowired
	private PostImageRepository postImageRepository;

	@BeforeEach
	void beforeEach() {
		postImageRepository.deleteAll();
		emojiRepository.deleteAll();
		voiceRepository.deleteAll();
		commentRepository.deleteAll();
		postRepository.deleteAll();
		memoryRepository.deleteAll();
		eventRepository.deleteAll();
		associateRepository.deleteAll();
		communityRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("포스트 상세 조회")
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		Post post = Post.builder()
			.content("test")
			.memory(memory)
			.associate(associate)
			.build();
		postRepository.save(post);

		Voice voice = Voice.builder()
			.name("test")
			.associate(associate)
			.temporary(false)
			.url("www.test.com")
			.build();
		voiceRepository.save(voice);

		Emoji emoji = Emoji.builder()
			.name("test")
			.associate(associate)
			.url("www.test.com")
			.build();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		//when
		SearchPostResponse response = postService.search(community.getId(), memory.getId(), associate.getId(), post.getId());

		//then
		assertThat(response.content()).isEqualTo("test");

		// author 관련 검증
		assertThat(response.author()).isNotNull();
		assertThat(response.author().getId()).isEqualTo(associate.getId());
		assertThat(response.author().getNickname()).isEqualTo(associate.getNickname());
		assertThat(response.author().getImageUrl()).isEqualTo(associate.getProfileImageUrl());

		// pictures 검증
		assertThat(response.pictures()).isNotNull();
		assertThat(response.pictures()).isEmpty();

		// comments 검증
		assertThat(response.comments()).isNotNull();
		// EMOJI, VOICE
		assertThat(response.comments().getEmojis()).hasSize(1);
		assertThat(response.comments().getVoices()).hasSize(1);
		assertThat(response.comments().getTemporaryVoices()).isEmpty();
	}

	@Test
	@DisplayName("포스트 목록 조회")
	void searchAllTest(){
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		Post post = Post.builder()
			.content("test")
			.memory(memory)
			.associate(associate)
			.build();
		postRepository.save(post);

		Voice voice = Voice.builder()
			.name("test")
			.associate(associate)
			.temporary(false)
			.url("www.test.com")
			.build();
		voiceRepository.save(voice);

		Emoji emoji = Emoji.builder()
			.name("test")
			.associate(associate)
			.url("www.test.com")
			.build();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		Pageable pageable = PageRequest.of(0, 10);

		//when
		SearchAllPostResponse response = postService.searchAll(community.getId(), memory.getId(), associate.getId(), pageable, null);

		//then
		assertThat(response.cursor()).isNull();
		assertThat(response.hasNext()).isFalse();
		assertThat(response.posts().getFirst().content()).isEqualTo("test");

		// author 관련 검증
		assertThat(response.posts().getFirst().author()).isNotNull();
		assertThat(response.posts().getFirst().author().getId()).isEqualTo(associate.getId());
		assertThat(response.posts().getFirst().author().getNickname()).isEqualTo(associate.getNickname());
		assertThat(response.posts().getFirst().author().getImageUrl()).isEqualTo(associate.getProfileImageUrl());

		// pictures 검증
		assertThat(response.posts().getFirst().pictures()).isNotNull();
		assertThat(response.posts().getFirst().pictures()).isEmpty();

		// comments 검증
		assertThat(response.posts().getFirst().comments()).isNotNull();
		// EMOJI, VOICE
		assertThat(response.posts().getFirst().comments().getEmojis()).hasSize(1);
		assertThat(response.posts().getFirst().comments().getVoices()).hasSize(1);
		assertThat(response.posts().getFirst().comments().getTemporaryVoices()).isEmpty();
	}

	@Test
	@DisplayName("포스트 목록 cursor")
	void searchAllCursorTest(){
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		Post post = Post.builder()
			.content("test")
			.memory(memory)
			.associate(associate)
			.build();
		postRepository.save(post);

		Voice voice = Voice.builder()
			.name("test")
			.associate(associate)
			.temporary(false)
			.url("www.test.com")
			.build();
		voiceRepository.save(voice);

		Emoji emoji = Emoji.builder()
			.name("test")
			.associate(associate)
			.url("www.test.com")
			.build();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		Pageable pageable = PageRequest.of(0, 1);

		//when
		SearchAllPostResponse response = postService.searchAll(community.getId(), memory.getId(), associate.getId(), pageable, null);

		//then
		assertThat(response.cursor()).isNotNull();
		assertThat(response.hasNext()).isTrue();
	}


	@Test
	@DisplayName("포스트 생성")
	public void createTest() throws IOException {
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		ClassPathResource resource = new ClassPathResource("static/test-images/ooh.png");
		MockMultipartFile file = new MockMultipartFile(
			"image",
			resource.getFilename(),
			"image/png",
			resource.getInputStream()
		);

		String content = "test";

		//when
		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		Post post = postRepository.findAll().get(0);

		//then
		assertThat(post.getContent()).isEqualTo(content);
	}

	@Test
	@DisplayName("중복 이미지는 저장할 수 없다")
	public void createHashTest() throws IOException {
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		ClassPathResource resource = new ClassPathResource("static/test-images/ooh.png");
		MockMultipartFile file1 = new MockMultipartFile(
			"image",
			resource.getFilename(),
			"image/png",
			resource.getInputStream()
		);

		ClassPathResource resource2 = new ClassPathResource("static/test-images/ooh2.png");
		MockMultipartFile file2 = new MockMultipartFile(
			"image",
			resource2.getFilename(),
			"image/png",
			resource2.getInputStream()
		);

		String content = "test";

		//when & then
		assertThrows(DataIntegrityViolationException.class, () -> {
			postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file1, file2));
		});
	}

	@Test
	@DisplayName("포스트 수정")
	public void updateTest() throws IOException {
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		ClassPathResource resource = new ClassPathResource("static/test-images/ooh.png");
		MockMultipartFile file = new MockMultipartFile(
			"image",
			resource.getFilename(),
			"image/png",
			resource.getInputStream()
		);

		String content = "change";

		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		Post post = postRepository.findAll().get(0);

		//when
		postService.update(community.getId(), memory.getId(), associate.getId(), post.getId(), content, List.of(), List.of(file));

		//then
		assertThat(post.getContent()).isEqualTo(content);
		assertThat(postImageRepository.findAll().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("포스트 삭제")
	public void deletePostTest() throws IOException {
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

		Event event = Event.builder()
			.associate(associate)
			.community(community)
			.description("example")
			.title("example")
			.location(Location.builder()
				.name("test")
				.address("test")
				.code(1)
				.latitude(new BigDecimal("123.45"))
				.longitude(new BigDecimal("123.45"))
				.build())
			.period(Period.builder()
				.startTime(LocalDateTime.now())
				.endTime(LocalDateTime.now())
				.build())
			.build();
		eventRepository.save(event);

		Memory memory = Memory.builder()
			.event(event)
			.build();
		memoryRepository.save(memory);

		Post post = Post.builder()
			.content("test")
			.memory(memory)
			.associate(associate)
			.build();
		postRepository.save(post);

		ClassPathResource resource = new ClassPathResource("static/test-images/ooh.png");
		MockMultipartFile file = new MockMultipartFile(
			"image",
			resource.getFilename(),
			"image/png",
			resource.getInputStream()
		);

		List<PostImage> images = postService.saveImages(post, List.of(file));
		postImageRepository.saveAll(images);

		Comment comment = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.EMOJI)
			.build();
		commentRepository.save(comment);

		// when
		postService.delete(community.getId(), memory.getId(), associate.getId(), post.getId());

		// then
		Post deletedPost = postRepository.findById(post.getId()).orElseThrow();
		assertThat(deletedPost.getDeletedAt()).isNotNull();

		List<PostImage> deletedImages = postImageRepository.findByPostIdAndDeletedAtNull(post.getId());
		assertThat(deletedImages).isEmpty();

		Comment deletedComment = commentRepository.findById(comment.getId()).orElseThrow();
		assertThat(deletedComment.getDeletedAt()).isNotNull();
	}

}
