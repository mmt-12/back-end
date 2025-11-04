package com.memento.server.spring.api.service.comment;

import static com.memento.server.config.MinioProperties.FileType.VOICE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.service.comment.CommentService;
import com.memento.server.api.service.comment.dto.request.CommentDeleteServiceRequest;
import com.memento.server.api.service.comment.dto.request.EmojiCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.TemporaryVoiceCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.VoiceCommentCreateServiceRequest;
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.fcm.FCMEventPublisher;
import com.memento.server.comment.CommentFixtures;
import com.memento.server.common.exception.MementoException;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_AUTHORITY;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.COMMENT_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.POST_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.VOICE_NOT_FOUND;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.memento.server.associate.AssociateFixtures;
import com.memento.server.common.fixture.CommonFixtures;
import com.memento.server.community.CommunityFixtures;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.community.Community;
import com.memento.server.domain.community.CommunityRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.member.Member;
import com.memento.server.domain.member.MemberRepository;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;
import com.memento.server.emoji.EmojiFixtures;
import com.memento.server.voice.VoiceFixtures;
import com.memento.server.memory.MemoryFixtures;
import com.memento.server.member.MemberFixtures;
import com.memento.server.post.PostFixtures;
import com.memento.server.spring.api.service.IntegrationsTestSupport;

public class CommentServiceTest extends IntegrationsTestSupport {

	@Autowired
	private CommentService commentService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CommunityRepository communityRepository;

	@Autowired
	private AssociateRepository associateRepository;

	@Autowired
	private EmojiRepository emojiRepository;

	@Autowired
	private VoiceRepository voiceRepository;

	@Autowired
	private MemoryRepository memoryRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private MinioProperties minioProperties;

	@MockitoBean
	private FCMEventPublisher fcmEventPublisher;

	@MockitoBean
	private AchievementEventPublisher achievementEventPublisher;

	@AfterEach
	public void tearDown() {
		commentRepository.deleteAllInBatch();
		postRepository.deleteAllInBatch();
		memoryRepository.deleteAllInBatch();
		emojiRepository.deleteAllInBatch();
		voiceRepository.deleteAllInBatch();
		associateRepository.deleteAllInBatch();
		communityRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("이모지 댓글을 생성한다.")
	void createEmojiComment() {
		// given
		Fixtures fixtures = createFixtures();
		EmojiCommentCreateServiceRequest request = EmojiCommentCreateServiceRequest.of(
			fixtures.emoji.getId(), fixtures.post().getId(), fixtures.associate().getId());

		// when
		commentService.createEmojiComment(request);

		// then
		Optional<Comment> foundComment = commentRepository.findAll().stream().findFirst();
		assertThat(foundComment).isPresent();
		assertThat(foundComment.get().getAssociate().getId()).isEqualTo(fixtures.associate().getId());
		assertThat(foundComment.get().getPost().getId()).isEqualTo(fixtures.post().getId());
		assertThat(foundComment.get().getUrl()).isEqualTo(fixtures.emoji.getUrl());
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 이모지 댓글 생성 시 예외가 발생한다.")
	void createEmojiCommentWithNonExistentAssociate() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentAssociateId = 999L;
		EmojiCommentCreateServiceRequest request = EmojiCommentCreateServiceRequest.of(
			fixtures.emoji.getId(), fixtures.post().getId(), nonExistentAssociateId);

		// when & then
		assertThatThrownBy(() -> commentService.createEmojiComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Emoji로 이모지 댓글 생성 시 예외가 발생한다.")
	void createEmojiCommentWithNonExistentEmoji() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentEmojiId = 999L;
		EmojiCommentCreateServiceRequest request = EmojiCommentCreateServiceRequest.of(
			nonExistentEmojiId, fixtures.post().getId(), fixtures.associate().getId());

		// when & then
		assertThatThrownBy(() -> commentService.createEmojiComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(EMOJI_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Post로 이모지 댓글 생성 시 예외가 발생한다.")
	void createEmojiCommentWithNonExistentPost() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentPostId = 999L;
		EmojiCommentCreateServiceRequest request = EmojiCommentCreateServiceRequest.of(
			fixtures.emoji.getId(), nonExistentPostId, fixtures.associate().getId());

		// when & then
		assertThatThrownBy(() -> commentService.createEmojiComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(POST_NOT_FOUND);
	}

	@Test
	@DisplayName("보이스 댓글을 생성한다.")
	void createVoiceComment() {
		// given
		Fixtures fixtures = createFixtures();
		VoiceCommentCreateServiceRequest request = VoiceCommentCreateServiceRequest.of(
			fixtures.permanentVoice.getId(), fixtures.post().getId(), fixtures.associate().getId());

		// when
		commentService.createVoiceComment(request);

		// then
		Optional<Comment> foundComment = commentRepository.findAll().stream().findFirst();
		assertThat(foundComment).isPresent();
		assertThat(foundComment.get().getAssociate().getId()).isEqualTo(fixtures.associate().getId());
		assertThat(foundComment.get().getPost().getId()).isEqualTo(fixtures.post().getId());
		assertThat(foundComment.get().getUrl()).isEqualTo(fixtures.permanentVoice.getUrl());
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 보이스 댓글 생성 시 예외가 발생한다.")
	void createVoiceCommentWithNonExistentAssociate() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentAssociateId = 999L;
		VoiceCommentCreateServiceRequest request = VoiceCommentCreateServiceRequest.of(
			fixtures.permanentVoice.getId(), fixtures.post().getId(), nonExistentAssociateId);

		// when & then
		assertThatThrownBy(() -> commentService.createVoiceComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Voice로 보이스 댓글 생성 시 예외가 발생한다.")
	void createVoiceCommentWithNonExistentVoice() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentVoiceId = 999L;
		VoiceCommentCreateServiceRequest request = VoiceCommentCreateServiceRequest.of(
			nonExistentVoiceId, fixtures.post().getId(), fixtures.associate().getId());

		// when & then
		assertThatThrownBy(() -> commentService.createVoiceComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(VOICE_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Post로 보이스 댓글 생성 시 예외가 발생한다.")
	void createVoiceCommentWithNonExistentPost() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentPostId = 999L;
		VoiceCommentCreateServiceRequest request = VoiceCommentCreateServiceRequest.of(
			fixtures.permanentVoice.getId(), nonExistentPostId, fixtures.associate().getId());

		// when & then
		assertThatThrownBy(() -> commentService.createVoiceComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(POST_NOT_FOUND);
	}

	@Test
	@DisplayName("일회용 보이스 댓글을 생성한다.")
	void createTemporaryVoiceComment() {
		// given
		Fixtures fixtures = createFixtures();
		MultipartFile voiceFile = CommonFixtures.voiceFile();
		TemporaryVoiceCommentCreateServiceRequest request = TemporaryVoiceCommentCreateServiceRequest.of(
			fixtures.post().getId(), fixtures.associate().getId(), voiceFile);

		String url = CommonFixtures.mockUrl(minioProperties, voiceFile, VOICE);
		given(minioService.createFile(voiceFile, VOICE)).willReturn(url);

		// when
		commentService.createTemporaryVoiceComment(request);

		// then
		Optional<Comment> foundComment = commentRepository.findAll().stream().findFirst();
		assertThat(foundComment).isPresent();
		assertThat(foundComment.get().getAssociate().getId()).isEqualTo(fixtures.associate().getId());
		assertThat(foundComment.get().getPost().getId()).isEqualTo(fixtures.post().getId());
		assertThat(foundComment.get().getUrl()).isEqualTo(url);

		verify(minioService).createFile(any(), any());
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 일회용 보이스 댓글 생성 시 예외가 발생한다.")
	void createTemporaryVoiceCommentWithNonExistentAssociate() {
		// given
		Fixtures fixtures = createFixtures();
		MultipartFile voiceFile = CommonFixtures.voiceFile();
		Long nonExistentAssociateId = 999L;
		TemporaryVoiceCommentCreateServiceRequest request = TemporaryVoiceCommentCreateServiceRequest.of(
			fixtures.post().getId(), nonExistentAssociateId, voiceFile);

		// when & then
		assertThatThrownBy(() -> commentService.createTemporaryVoiceComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Post로 일회용 보이스 댓글 생성 시 예외가 발생한다.")
	void createTemporaryVoiceCommentWithNonExistentPost() {
		// given
		Fixtures fixtures = createFixtures();
		MultipartFile voiceFile = CommonFixtures.voiceFile();
		Long nonExistentPostId = 999L;
		TemporaryVoiceCommentCreateServiceRequest request = TemporaryVoiceCommentCreateServiceRequest.of(
			nonExistentPostId, fixtures.associate().getId(), voiceFile);

		// when & then
		assertThatThrownBy(() -> commentService.createTemporaryVoiceComment(request))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(POST_NOT_FOUND);
	}

	@Test
	@DisplayName("이모지 댓글을 삭제한다.")
	void deleteEmojiComment() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = createEmojiComment(fixtures);
		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(comment.getId(),
			fixtures.associate().getId());

		// when
		commentService.deleteComment(deleteRequest);

		// then
		Optional<Comment> deletedComment = commentRepository.findByIdAndDeletedAtIsNull(comment.getId());
		assertThat(deletedComment).isNotPresent();

		verify(minioService, never()).removeFile(any());
	}

	@Test
	@DisplayName("영구 보이스 댓글을 삭제한다 (MinIO 파일 삭제하지 않음)")
	void deletePermanentVoiceComment() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = createPermanentVoiceComment(fixtures);

		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(comment.getId(),
			fixtures.associate().getId());

		// when
		commentService.deleteComment(deleteRequest);

		// then
		Optional<Comment> deletedComment = commentRepository.findByIdAndDeletedAtIsNull(comment.getId());
		assertThat(deletedComment).isNotPresent();

		Optional<Voice> foundVoice = voiceRepository.findByIdAndDeletedAtIsNull(fixtures.permanentVoice.getId());
		assertThat(foundVoice).isPresent();

		verify(minioService, never()).removeFile(any());
	}

	@Test
	@DisplayName("일회용 보이스 댓글을 삭제한다 (MinIO 파일도 삭제됨)")
	void deleteTemporaryVoiceComment() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = createTemporaryVoiceComment(fixtures);
		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(comment.getId(),
			fixtures.associate().getId());

		doNothing().when(minioService).removeFile(any());

		// when
		commentService.deleteComment(deleteRequest);

		// then
		Optional<Comment> deletedComment = commentRepository.findByIdAndDeletedAtIsNull(comment.getId());
		assertThat(deletedComment).isNotPresent();

		Optional<Voice> foundVoice = voiceRepository.findByUrlAndDeletedAtIsNull(fixtures.temporaryVoice().getUrl());
		assertThat(foundVoice).isNotPresent();

		verify(minioService, times(1)).removeFile(any());
	}

	@Test
	@DisplayName("존재하지 않는 Associate로 댓글 삭제 시 예외가 발생한다.")
	void deleteCommentWithNonExistentAssociate() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = createEmojiComment(fixtures);
		Long nonExistentAssociateId = 999L;
		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(
			comment.getId(), nonExistentAssociateId);

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(deleteRequest))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_FOUND);
	}

	@Test
	@DisplayName("존재하지 않는 Comment로 댓글 삭제 시 예외가 발생한다.")
	void deleteCommentWithNonExistentComment() {
		// given
		Fixtures fixtures = createFixtures();
		Long nonExistentCommentId = 999L;
		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(
			nonExistentCommentId, fixtures.associate().getId());

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(deleteRequest))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(COMMENT_NOT_FOUND);
	}

	@Test
	@DisplayName("댓글 작성자가 아닌 사용자가 댓글 삭제 시 예외가 발생한다.")
	void deleteCommentWithUnauthorizedUser() {
		// given
		Fixtures fixtures = createFixtures();
		Comment comment = createEmojiComment(fixtures);

		Associate unauthorizedAssociate = createFixtures().associate();
		CommentDeleteServiceRequest deleteRequest = CommentDeleteServiceRequest.of(
			comment.getId(), unauthorizedAssociate.getId());

		// when & then
		assertThatThrownBy(() -> commentService.deleteComment(deleteRequest))
			.isInstanceOf(MementoException.class)
			.extracting("errorCode")
			.isEqualTo(ASSOCIATE_NOT_AUTHORITY);
	}

	private record Fixtures(
		Member member,
		Community community,
		Associate associate,
		Memory memory,
		Post post,
		Emoji emoji,
		Voice permanentVoice,
		Voice temporaryVoice
	) {

	}

	private Fixtures createFixtures() {
		Member member = MemberFixtures.member();
		Community community = CommunityFixtures.community(member);
		Associate associate = AssociateFixtures.associate(member, community);
		Memory memory = MemoryFixtures.memory(community, associate);
		Post post = PostFixtures.post(memory, associate);
		Emoji emoji = EmojiFixtures.emoji(associate);
		Voice permanentVoice = VoiceFixtures.permanentVoice(associate);
		Voice temporaryVoice = VoiceFixtures.temporaryVoice(associate);

		Member savedMember = memberRepository.save(member);
		Community savedCommunity = communityRepository.save(community);
		Associate savedAssociate = associateRepository.save(associate);
		Memory savedMemory = memoryRepository.save(memory);
		Post savedPost = postRepository.save(post);
		Emoji savedEmoji = emojiRepository.save(emoji);
		Voice savedPermanentVoice = voiceRepository.save(permanentVoice);
		Voice savedTemporaryVoice = voiceRepository.save(temporaryVoice);
		return new Fixtures(savedMember, savedCommunity, savedAssociate, savedMemory, savedPost, savedEmoji,
			savedPermanentVoice, savedTemporaryVoice);
	}

	private Comment createEmojiComment(Fixtures fixtures) {
		Comment comment = CommentFixtures.emojiComment(fixtures.emoji, fixtures.post, fixtures.associate);
		return commentRepository.save(comment);
	}

	private Comment createPermanentVoiceComment(Fixtures fixtures) {
		Comment comment = CommentFixtures.permanentVoiceComment(fixtures.permanentVoice, fixtures.post,
			fixtures.associate);
		return commentRepository.save(comment);
	}

	private Comment createTemporaryVoiceComment(Fixtures fixtures) {
		Comment comment = CommentFixtures.temporaryVoiceComment(fixtures.temporaryVoice, fixtures.post,
			fixtures.associate);
		return commentRepository.save(comment);
	}
}