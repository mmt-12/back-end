package com.memento.server.api.service.post;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.post.dto.SearchAllPostResponse;
import com.memento.server.api.controller.post.dto.SearchPostResponse;
import com.memento.server.api.controller.post.dto.read.Achievement;
import com.memento.server.api.controller.post.dto.read.CommentAuthor;
import com.memento.server.api.controller.post.dto.read.Emoji;
import com.memento.server.api.controller.post.dto.read.PostAuthor;
import com.memento.server.api.controller.post.dto.read.TemporaryVoice;
import com.memento.server.api.controller.post.dto.read.Voice;
import com.memento.server.api.service.minio.MinioService;
import com.memento.server.api.service.post.dto.PostCommentDto;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.comment.Comment;
import com.memento.server.domain.comment.CommentRepository;
import com.memento.server.domain.comment.CommentType;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.memory.Memory;
import com.memento.server.domain.memory.MemoryRepository;
import com.memento.server.domain.post.Hash;
import com.memento.server.domain.post.Post;
import com.memento.server.domain.post.PostImage;
import com.memento.server.domain.post.PostImageRepository;
import com.memento.server.domain.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final AssociateRepository associateRepository;
	private final PostRepository postRepository;
	private final PostImageRepository postImageRepository;
	private final MemoryRepository memoryRepository;
	private final CommentRepository commentRepository;
	private final MinioService minioService;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	public Post validPost(Long memoryId, Long postId){
		Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
			.orElseThrow(() -> new MementoException(ErrorCodes.POST_NOT_FOUND));
		if(!memoryId.equals(post.getMemory().getId())){
			throw new MementoException(ErrorCodes.MEMORY_POST_NOT_MATCH);
		}

		return post;
	}

	public SearchPostResponse search(Long communityId, Long memoryId, Long associateId, Long postId) {
		Associate associate = validAssociate(communityId, associateId);
		Post post = validPost(memoryId, postId);

		Map<Post, List<PostImage>> imagesByPostId = postImageRepository
			.findAllByPostIdInAndDeletedAtNull(List.of(postId))
			.stream()
			.collect(Collectors.groupingBy(PostImage::getPost));

		Map<Long, List<PostCommentDto>> commentsByPostId = commentRepository
			.findPostCommentsByPostIds(List.of(postId), associate.getId())
			.stream()
			.collect(Collectors.groupingBy(PostCommentDto::getPostId));

		return mapToSearchPostResponse(post, associate, imagesByPostId, commentsByPostId);
	}

	public SearchAllPostResponse searchAll(Long communityId, Long memoryId, Long associateId, int size, Long cursor) {
		Associate associate = validAssociate(communityId, associateId);

		Pageable pageable = PageRequest.of(0, size+1);

		List<Post> posts = postRepository.findAllByMemoryIdAndCursor(memoryId, cursor, pageable);

		List<Long> postIds = posts.stream().limit(size).map(Post::getId).toList();

		Map<Post, List<PostImage>> imagesByPostId = postImageRepository
			.findAllByPostIdInAndDeletedAtNull(postIds)
			.stream()
			.collect(Collectors.groupingBy(PostImage::getPost));

		Map<Long, List<PostCommentDto>> commentsByPostId = commentRepository
			.findPostCommentsByPostIds(postIds, associate.getId())
			.stream()
			.collect(Collectors.groupingBy(
				PostCommentDto::getPostId,
				LinkedHashMap::new,
				Collectors.toList()
			));

		List<SearchPostResponse> responses = posts.stream().limit(size)
			.map(post -> mapToSearchPostResponse(post, associate, imagesByPostId, commentsByPostId))
			.toList();

		Long lastCursor = null;
		boolean hasNext = false;
		if(posts.size() == pageable.getPageSize()){
			lastCursor = posts.getLast().getId();
			hasNext = true;
		}

		return SearchAllPostResponse.builder()
			.nextCursor(lastCursor)
			.hasNext(hasNext)
			.posts(responses)
			.build();
	}

	@Transactional
	public void create(Long communityId, Long memoryId, Long associateId, String content, List<MultipartFile> pictures) {
		Associate associate = validAssociate(communityId, associateId);

		Memory memory = memoryRepository.findByIdAndDeletedAtIsNull(memoryId)
			.orElseThrow(() -> new MementoException(ErrorCodes.MEMORY_NOT_FOUND));

		if(!memory.getEvent().getCommunity().getId().equals(communityId)){
			throw new MementoException(ErrorCodes.COMMUNITY_NOT_MATCH);
		}

		Post post = postRepository.save(Post.builder()
				.associate(associate)
				.memory(memory)
				.content(content)
			.build());

		List<PostImage> images = saveImages(post, pictures);
		postImageRepository.saveAll(images);
	}

	@Transactional
	public void update(Long communityId, Long memoryId, Long associateId, Long postId, String content, List<Long> oldPictures, List<MultipartFile> newPictures) {
		Associate associate = validAssociate(communityId, associateId);
		Post post = validPost(memoryId, postId);

		if(!post.getAssociate().equals(associate)){
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		if(content != null){
			post.updateContent(content);
		}

		List<PostImage> images = postImageRepository.findByPostIdAndDeletedAtNull(postId);
		List<PostImage> deleteImages = images.stream()
			.filter(image -> !oldPictures.contains(image.getId()))
			.toList();

		for(PostImage image : deleteImages){
			image.markDeleted();
		}

		if(newPictures != null){
			List<PostImage> newImages = saveImages(post, newPictures);
			postImageRepository.saveAll(newImages);
		}
	}

	@Transactional
	public void delete(Long communityId, Long memoryId, Long associateId, Long postId) {
		Associate associate = validAssociate(communityId, associateId);
		Post post = validPost(memoryId, postId);

		if(!post.getAssociate().equals(associate)){
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		Map<Post, List<PostImage>> imagesByPostId = postImageRepository
			.findAllByPostIdInAndDeletedAtNull(List.of(postId))
			.stream()
			.collect(Collectors.groupingBy(PostImage::getPost));

		for(PostImage image : imagesByPostId.get(post)){
			image.markDeleted();
		}

		List<Comment> comments = commentRepository.findAllByPostIdAndDeletedAtNull(post.getId());
		for(Comment comment : comments){
			comment.markDeleted();
		}

		post.markDeleted();
	}

	private SearchPostResponse mapToSearchPostResponse(Post post, Associate associate,
		Map<Post, List<PostImage>> imagesByPostId,
		Map<Long, List<PostCommentDto>> commentsByPostId) {

		List<PostImage> images = imagesByPostId.getOrDefault(post, List.of());
		List<PostCommentDto> comments = commentsByPostId.getOrDefault(post.getId(), List.of());

		// Emoji 변환
		List<Emoji> emojis = comments.stream()
			.filter(c -> c.getType() == CommentType.EMOJI)
			.collect(Collectors.groupingBy(PostCommentDto::getUrl,
				LinkedHashMap::new,
				Collectors.toList()))
			.entrySet().stream()
			.map(entry -> {
				String url = entry.getKey();
				List<PostCommentDto> dtoList = entry.getValue();
				List<CommentAuthor> authors = dtoList.stream()
					.map(CommentAuthor::from)
					.toList();
				boolean isInvolved = dtoList.stream()
					.anyMatch(dto -> dto.getAssociate().getId().equals(associate.getId()));

				return Emoji.builder()
					.id(dtoList.get(0).getId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.isInvolved(isInvolved)
					.build();
			})
			.collect(Collectors.toList());

		// Voice 변환
		List<Voice> voices = comments.stream()
			.filter(c -> c.getType() == CommentType.VOICE && !c.getIsTemporary())
			.collect(Collectors.groupingBy(PostCommentDto::getUrl,
				LinkedHashMap::new,
				Collectors.toList()))
			.entrySet().stream()
			.map(entry -> {
				String url = entry.getKey();
				List<PostCommentDto> dtoList = entry.getValue();
				List<CommentAuthor> authors = dtoList.stream()
					.map(CommentAuthor::from)
					.toList();
				boolean isInvolved = dtoList.stream()
					.anyMatch(dto -> dto.getAssociate().getId().equals(associate.getId()));

				return Voice.builder()
					.id(dtoList.get(0).getId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.isInvolved(isInvolved)
					.build();
			})
			.collect(Collectors.toList());

		// TemporaryVoice 변환
		List<TemporaryVoice> temporaryVoices = comments.stream()
			.filter(c -> c.getType() == CommentType.VOICE && c.getIsTemporary())
			.collect(Collectors.groupingBy(PostCommentDto::getUrl,
				LinkedHashMap::new,
				Collectors.toList()))
			.entrySet().stream()
			.map(entry -> {
				String url = entry.getKey();
				List<PostCommentDto> dtoList = entry.getValue();
				List<CommentAuthor> authors = dtoList.stream()
					.map(CommentAuthor::from)
					.toList();

				return TemporaryVoice.builder()
					.id(dtoList.get(0).getId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.build();
			})
			.collect(Collectors.toList());

		// === CommentResponse ===
		SearchPostResponse.Comment commentsResponse = SearchPostResponse.Comment.builder()
			.emojis(emojis)
			.voices(voices)
			.temporaryVoices(temporaryVoices)
			.build();

		// === 최종 Response ===
		return SearchPostResponse.builder()
			.id(post.getId())
			.author(PostAuthor.builder()
				.id(post.getAssociate().getId())
				.imageUrl(post.getAssociate().getProfileImageUrl())
				.nickname(post.getAssociate().getNickname())
				.achievement(post.getAssociate().getAchievement() == null ? null :
					Achievement.builder()
						.id(post.getAssociate().getAchievement().getId())
						.name(post.getAssociate().getAchievement().getName())
						.build())
				.build())
			.content(post.getContent())
			.pictures(images.stream().map(PostImage::getUrl).toList())
			.comments(commentsResponse)
			.build();
	}

	public List<PostImage> saveImages(Post post, List<MultipartFile> pictures) {
		List<PostImage> images = new ArrayList<>();
		List<Hash> hashes = new ArrayList<>();
		for(MultipartFile image : pictures){
			Hash hash = null;
			String url = null;
			try{
				url = minioService.createFile(image, MinioProperties.FileType.POST);
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] bytes = image.getBytes();
				byte[] hashBytes = digest.digest(bytes);

				StringBuilder sb = new StringBuilder();
				for(byte b : hashBytes) {
					sb.append(String.format("%02x", b));
				}
				hash = Hash.builder()
					.hash(sb.toString())
					.build();
			}catch (Exception e){
				throw new MementoException(ErrorCodes.POST_IMAGE_SAVE_FAIL);
			}

			images.add(PostImage.builder()
					.url(url)
					.post(post)
					.hash(hash)
				.build());
			hashes.add(hash);
		}

		Set<String> uniqueHashes = new HashSet<>();
		for (Hash hash : hashes) {
			if (!uniqueHashes.add(hash.getHash())) {
				throw new MementoException(ErrorCodes.POST_IMAGE_DUPLICATED);
			}
		}

		List<PostImage> duplicatedImage = postImageRepository.findAllByHashInAndDeletedAtIsNull(hashes);
		if(!duplicatedImage.isEmpty()){
			throw new MementoException(ErrorCodes.POST_IMAGE_DUPLICATED);
		}

		return images;
	}
}
