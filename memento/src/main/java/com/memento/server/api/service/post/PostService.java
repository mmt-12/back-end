package com.memento.server.api.service.post;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
import com.memento.server.api.service.achievement.AchievementEventPublisher;
import com.memento.server.api.service.eventMessage.EventMessagePublisher;
import com.memento.server.api.service.eventMessage.dto.PostNotification;
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
import com.memento.server.domain.post.PostImageAchievementEvent;
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
	private final AchievementEventPublisher achievementEventPublisher;
	private final EventMessagePublisher eventMessagePublisher;

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
			.findAllByPostIdInAndDeletedAtNullOrderByCreatedAtDesc(List.of(postId))
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
			.findAllByPostIdInAndDeletedAtNullOrderByCreatedAtDesc(postIds)
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

		achievementEventPublisher.publishPostImageAchievement(PostImageAchievementEvent.from(associate.getId()));
		eventMessagePublisher.publishNotification(PostNotification.from(memory));
	}

	@Transactional
	public void update(Long communityId, Long memoryId, Long associateId, Long postId, String content, List<String> oldPictures, List<MultipartFile> newPictures) {
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
			.filter(image -> !oldPictures.contains(image.getUrl()))
			.toList();

		for(PostImage image : deleteImages){
			image.markDeleted();
		}

		if(newPictures != null){
			List<PostImage> newImages = saveImages(post, newPictures);
			postImageRepository.saveAll(newImages);
		}

		achievementEventPublisher.publishPostImageAchievement(PostImageAchievementEvent.from(associate.getId()));
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
					.id(dtoList.get(0).getReactionId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.count(dtoList.size())
					.isInvolved(isInvolved)
					.build();
			})
			.sorted(Comparator.comparing(Emoji::getCount).reversed())
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
					.id(dtoList.get(0).getReactionId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.count(dtoList.size())
					.isInvolved(isInvolved)
					.build();
			})
			.sorted(Comparator.comparing(Voice::getCount).reversed())
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
					.id(dtoList.get(0).getReactionId())
					.url(url)
					.name(dtoList.get(0).getName())
					.authors(authors)
					.count(dtoList.size())
					.build();
			})
			.sorted(Comparator.comparing(TemporaryVoice::getCount).reversed())
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
			.createdAt(post.getCreatedAt())
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

		for(MultipartFile picture : pictures) {
			try {
				InputStream is = picture.getInputStream();
				BufferedImage image = ImageIO.read(is);
				if (image == null) {
					throw new IllegalArgumentException("이미지 읽기 실패");
				}

				int width = image.getWidth();
				int height = image.getHeight();
				byte[] pixels = new byte[width * height * 4];
				int idx = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int rgb = image.getRGB(x, y);
						pixels[idx++] = (byte) ((rgb >> 24) & 0xFF);
						pixels[idx++] = (byte) ((rgb >> 16) & 0xFF);
						pixels[idx++] = (byte) ((rgb >> 8) & 0xFF);
						pixels[idx++] = (byte) (rgb & 0xFF);
					}
				}

				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hashBytes = digest.digest(pixels);

				StringBuilder sb = new StringBuilder();
				for (byte b : hashBytes) {
					sb.append(String.format("%02x", b));
				}

				hashes.add(Hash.builder().hash(sb.toString()).build());

			} catch (Exception e) {
				throw new MementoException(ErrorCodes.POST_IMAGE_SAVE_FAIL);
			}
		}

		List<PostImage> existingImages = postImageRepository.findAllByHashInAndDeletedAtIsNull(hashes);
		Map<Hash, List<PostImage>> existingMap = existingImages.stream()
			.collect(Collectors.groupingBy(PostImage::getHash));

		Map<Hash, PostImage> hashToImage = new HashMap<>();

		for (int i = 0; i < pictures.size(); i++) {
			MultipartFile image = pictures.get(i);
			Hash hash = hashes.get(i);

			if (existingMap.containsKey(hash)) {
				images.add(PostImage.builder()
					.url(existingMap.get(hash).get(0).getUrl())
					.post(post)
					.hash(hash)
					.build());
			} else if (hashToImage.containsKey(hash)) {
				images.add(PostImage.builder()
					.url(hashToImage.get(hash).getUrl())
					.post(post)
					.hash(hash)
					.build());
			} else {
				String url = minioService.createFile(image, MinioProperties.FileType.POST);
				PostImage newImage = PostImage.builder()
					.url(url)
					.post(post)
					.hash(hash)
					.build();

				hashToImage.put(hash, newImage);
				images.add(newImage);
			}
		}
		return images;
	}
}
