package com.memento.server.api.service.comment;

import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_AUTHORITY;
import static com.memento.server.common.error.ErrorCodes.ASSOCIATE_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.COMMENT_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.EMOJI_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.POST_NOT_FOUND;
import static com.memento.server.common.error.ErrorCodes.VOICE_NOT_FOUND;
import static com.memento.server.config.MinioProperties.FileType.VOICE;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.comment.dto.request.CommentDeleteServiceRequest;
import com.memento.server.api.service.comment.dto.request.EmojiCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.TemporaryVoiceCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.VoiceCommentCreateServiceRequest;
import com.memento.server.api.service.minio.MinioService;
import com.memento.server.common.exception.MementoException;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.comment.CommentType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.emoji.Emoji;
import com.memento.server.domain.emoji.EmojiRepository;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostRepository;
import com.memento.server.domain.reaction.ReactionAchievementEvent;
import com.memento.server.domain.voice.Voice;
import com.memento.server.domain.voice.VoiceRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

	private final EmojiRepository emojiRepository;
	private final VoiceRepository voiceRepository;
	private final AssociateRepository associateRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final MinioService minioService;
	private final AchievementEventPublisher achievementEventPublisher;

	@Transactional
	public void createEmojiComment(EmojiCommentCreateServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		Emoji emoji = emojiRepository.findByIdAndDeletedAtIsNull(request.emojiId())
			.orElseThrow(() -> new MementoException(EMOJI_NOT_FOUND));
		Post post = postRepository.findByIdAndDeletedAtIsNull(request.postId())
			.orElseThrow(() -> new MementoException(POST_NOT_FOUND));

		Comment comment = Comment.createEmojiComment(emoji.getUrl(), post, associate);
		commentRepository.save(comment);

		achievementEventPublisher.publishReactionAchievement(
			ReactionAchievementEvent.fromUse(associate.getId(), ReactionAchievementEvent.Type.USE));
	}

	@Transactional
	public void createVoiceComment(VoiceCommentCreateServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		Voice voice = voiceRepository.findByIdAndDeletedAtIsNull(request.voiceId())
			.orElseThrow(() -> new MementoException(VOICE_NOT_FOUND));
		Post post = postRepository.findByIdAndDeletedAtIsNull(request.postId())
			.orElseThrow(() -> new MementoException(POST_NOT_FOUND));

		Comment comment = Comment.createVoiceComment(voice.getUrl(), post, associate);
		commentRepository.save(comment);

		achievementEventPublisher.publishReactionAchievement(ReactionAchievementEvent.fromUse(associate.getId(), ReactionAchievementEvent.Type.USE));
	}

	@Transactional
	public void createTemporaryVoiceComment(TemporaryVoiceCommentCreateServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		Post post = postRepository.findByIdAndDeletedAtIsNull(request.postId())
			.orElseThrow(() -> new MementoException(POST_NOT_FOUND));

		String url = minioService.createFile(request.voice(), VOICE);
		Voice voice = Voice.createTemporary(url, associate);
		voiceRepository.save(voice);

		Comment comment = Comment.createVoiceComment(url, post, associate);
		commentRepository.save(comment);
	}

	@Transactional
	public void deleteComment(CommentDeleteServiceRequest request) {
		Associate associate = associateRepository.findByIdAndDeletedAtNull(request.associateId())
			.orElseThrow(() -> new MementoException(ASSOCIATE_NOT_FOUND));
		Comment comment = commentRepository.findByIdAndDeletedAtIsNull(request.commentId())
			.orElseThrow(() -> new MementoException(COMMENT_NOT_FOUND));

		if(!comment.getAssociate().getId().equals(associate.getId())) {
			throw new MementoException(ASSOCIATE_NOT_AUTHORITY);
		}

		if (comment.getType().equals(CommentType.VOICE)) {
			Voice voice = voiceRepository.findByUrlAndDeletedAtIsNull(comment.getUrl())
				.orElseThrow(() -> new MementoException(VOICE_NOT_FOUND));

			if (voice.isTemporary()) {
				minioService.removeFile(comment.getUrl());
				voice.markDeleted();
			}
		}

		comment.markDeleted();
	}
}
