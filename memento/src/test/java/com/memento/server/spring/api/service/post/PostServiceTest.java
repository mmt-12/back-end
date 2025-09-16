package com.memento.server.spring.api.service.post;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.post.dto.SearchAllPostResponse;
import com.memento.server.api.controller.post.dto.SearchPostResponse;
import com.memento.server.api.service.post.PostService;
import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.exception.MementoException;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
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
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.event.EventFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.memory.MemoryFixtures;
import com.memento.server.post.PostFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;
import com.memento.server.voice.VoiceFixtures;

public class PostServiceTest extends IntegrationsTestSupport {

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
		commentRepository.deleteAll();
		emojiRepository.deleteAll();
		voiceRepository.deleteAll();
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
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		Voice voice = VoiceFixtures.permanentVoice("test", "test.mp3", associate);
		voiceRepository.save(voice);

		Emoji emoji = EmojiFixtures.emoji();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url(emoji.getUrl())
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url(voice.getUrl())
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		//when
		SearchPostResponse response = postService.search(community.getId(), memory.getId(), associate.getId(), post.getId());

		//then
		assertThat(response.content()).isEqualTo("content");

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
		assertThat(response.comments().getEmojis().getFirst().getId()).isEqualTo(emoji.getId());
		assertThat(response.comments().getEmojis().getFirst().getUrl()).isEqualTo(emoji.getUrl());
		assertThat(response.comments().getEmojis().getFirst().getName()).isEqualTo(emoji.getName());
		assertThat(response.comments().getEmojis().getFirst().getAuthors().getFirst().getCommentId()).isEqualTo(comment1.getId());
		assertThat(response.comments().getVoices()).hasSize(1);
		assertThat(response.comments().getVoices().getFirst().getName()).isEqualTo(voice.getName());
		assertThat(response.comments().getVoices().getFirst().getAuthors().getFirst().getCommentId()).isEqualTo(comment2.getId());
		assertThat(response.comments().getTemporaryVoices()).isEmpty();
	}

	@Test
	@DisplayName("다른 기억의 포스트는 조회할 수 없습니다")
	void searchWithDifferentMemoryTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Event event2 = EventFixtures.event(community, associate);
		eventRepository.save(event2);

		Memory memory2 = MemoryFixtures.memory(event2);
		memoryRepository.save(memory2);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		Voice voice = VoiceFixtures.permanentVoice("test", "test.mp3", associate);
		voiceRepository.save(voice);

		Emoji emoji = EmojiFixtures.emoji();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("https://example.com/image.png")
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("test.mp3")
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		// when & then
		assertThrows(MementoException.class, () -> postService.search(community.getId(), memory2.getId(), associate.getId(), post.getId()));


	}

	@Test
	@DisplayName("포스트 목록 조회")
	void searchAllTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		Voice voice = VoiceFixtures.permanentVoice("test", "test.mp3", associate);
		voiceRepository.save(voice);

		Emoji emoji = EmojiFixtures.emoji();
		emojiRepository.save(emoji);

		Comment comment1 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("https://example.com/image.png")
			.type(CommentType.EMOJI)
			.build();

		Comment comment2 = Comment.builder()
			.associate(associate)
			.post(post)
			.url("test.mp3")
			.type(CommentType.VOICE)
			.build();
		commentRepository.save(comment1);
		commentRepository.save(comment2);

		//when
		SearchAllPostResponse response = postService.searchAll(community.getId(), memory.getId(), associate.getId(), 10, null);

		//then
		assertThat(response.nextCursor()).isNull();
		assertThat(response.hasNext()).isFalse();
		assertThat(response.posts().getFirst().content()).isEqualTo("content");

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
	@DisplayName("포스트 목록 조회 페이지네이션")
	void searchAllWithPaginationTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		Post post2 = PostFixtures.post(memory, associate);
		postRepository.save(post2);

		//when
		SearchAllPostResponse response = postService.searchAll(community.getId(), memory.getId(), associate.getId(), 1, null);

		//then
		assertThat(response.posts()).hasSize(1);
		assertThat(response.nextCursor()).isEqualTo(post.getId());
		assertThat(response.hasNext()).isTrue();
	}

	@Test
	@DisplayName("포스트 생성")
	public void createTest() {
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "test";

		//when
		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		Post post = postRepository.findAll().get(0);

		//then
		assertThat(post.getContent()).isEqualTo(content);
	}

	@Test
	@DisplayName("존재하지 않는 기억에서 포스트를 생성할 수 없다")
	public void createWithNotExistenceMemoryTest() {
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "test";

		//when & then
		assertThrows(MementoException.class, () -> postService.create(community.getId(), 1L, associate.getId(), content, List.of(file)));
	}

	@Test
	@DisplayName("다른 커뮤니티의 기억에서 포스트를 생성할 수 없다")
	public void createWithDifferentMemoryTest() {
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Community community2 = CommunityFixtures.community(member);
		communityRepository.save(community2);

		Associate associate2 = AssociateFixtures.associate(member, community2);
		associateRepository.save(associate2);

		Event event = EventFixtures.event(community2, associate2);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "test";

		//when & then
		assertThrows(MementoException.class, () -> postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file)));
	}

	@Test
	@DisplayName("중복 이미지는 저장할 수 없다")
	public void createHashTest() {
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		MultipartFile file1 = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url1 = "https://example.com/test.png";
		given(minioService.createFile(file1, MinioProperties.FileType.POST))
			.willReturn(url1);

		MultipartFile file2 = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url2 = "https://example.com/test.png";
		given(minioService.createFile(file2, MinioProperties.FileType.POST))
			.willReturn(url2);

		String content = "test";

		//when & then
		assertThrows(MementoException.class, () -> {
			postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file1, file2));
		});
	}

	@Test
	@DisplayName("포스트 수정")
	public void updateTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "change";

		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		Post post = postRepository.findAll().get(0);

		//when
		postService.update(community.getId(), memory.getId(), associate.getId(), post.getId(), content, List.of(), List.of(file));

		//then
		assertThat(post.getContent()).isEqualTo(content);
		assertThat(postImageRepository.findAll().get(0).getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("다른 참여자의 포스트는 수정할 수 없다")
	public void updateWithDifferentAssociateTest(){
		//given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		String content = "change";

		postService.create(community.getId(), memory.getId(), associate.getId(), content, List.of(file));

		Post post = postRepository.findAll().get(0);

		//when & then
		assertThrows(MementoException.class, () -> postService.update(community.getId(), memory.getId(), associate2.getId(), post.getId(), content, List.of(), List.of(file)));
	}

	@Test
	@DisplayName("포스트 삭제")
	public void deleteTest() {
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

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

	@Test
	@DisplayName("다른 참여자의 포스트는 삭제할 수 없다")
	public void deleteWithDifferentAssociateTest(){
		// given
		Member member = MemberFixtures.member();
		memberRepository.save(member);

		Community community = CommunityFixtures.community(member);
		communityRepository.save(community);

		Associate associate = AssociateFixtures.associate(member, community);
		associateRepository.save(associate);

		Associate associate2 = AssociateFixtures.associate(member, community);
		associateRepository.save(associate2);

		Event event = EventFixtures.event(community, associate);
		eventRepository.save(event);

		Memory memory = MemoryFixtures.memory(event);
		memoryRepository.save(memory);

		Post post = PostFixtures.post(memory, associate);
		postRepository.save(post);

		MultipartFile file = new MockMultipartFile("image", "test.png", "image/png", "test".getBytes());
		String url = "https://example.com/test.png";
		given(minioService.createFile(file, MinioProperties.FileType.POST))
			.willReturn(url);

		List<PostImage> images = postService.saveImages(post, List.of(file));
		postImageRepository.saveAll(images);

		Comment comment = Comment.builder()
			.associate(associate)
			.post(post)
			.url("www.test.com")
			.type(CommentType.EMOJI)
			.build();
		commentRepository.save(comment);

		// when & then
		assertThrows(MementoException.class, () -> postService.delete(community.getId(), memory.getId(), associate2.getId(), post.getId()));
	}
}
