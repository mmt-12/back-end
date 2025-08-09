package com.memento.server.api.service.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.memento.server.api.service.comment.dto.request.CommentDeleteServiceRequest;
import com.memento.server.api.service.comment.dto.request.EmojiCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.TemporaryVoiceCommentCreateServiceRequest;
import com.memento.server.api.service.comment.dto.request.VoiceCommentCreateServiceRequest;
import com.memento.server.domain.comment.CommentRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentService {

	private final CommentRepository commentRepository;

	public void createEmojiComment(EmojiCommentCreateServiceRequest request) {

	}

	public void createVoiceComment(VoiceCommentCreateServiceRequest request) {

	}

	public void createTemporaryVoiceComment(TemporaryVoiceCommentCreateServiceRequest request) {

	}

	public void deleteComment(CommentDeleteServiceRequest request) {

	}
}
