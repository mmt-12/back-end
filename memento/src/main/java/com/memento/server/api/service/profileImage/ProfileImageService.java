package com.memento.server.api.service.profileImage;

import static com.memento.server.config.MinioProperties.FileType.POST;
import static org.apache.commons.io.FilenameUtils.getExtension;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.api.service.minio.MinioService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.profileImage.ProfileImage;
import com.memento.server.domain.profileImage.ProfileImageRepository;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {

	private final AssociateRepository associateRepository;
	private final ProfileImageRepository profileImageRepository;
	private final MinioService minioService;
	private final MinioClient minioClient;
	private final MinioProperties minioProperties;

	public Associate validAssociate(Long communityId, Long associateId){
		Associate associate = associateRepository.findByIdAndDeletedAtNull(associateId)
			.orElseThrow(() -> new MementoException(ErrorCodes.ASSOCIATE_NOT_EXISTENCE));
		if(!communityId.equals(associate.getCommunity().getId())){
			throw new MementoException(ErrorCodes.ASSOCIATE_COMMUNITY_NOT_MATCH);
		}

		return associate;
	}

	@Transactional
	public void create(Long communityId, Long associateId, Long registrantId, MultipartFile image) {
		Associate associate = validAssociate(communityId, associateId);
		Associate registrant = validAssociate(communityId, registrantId);

		// todo minioService method 호출로 변경
		// String url = saveImage(associate, image);
		String url = minioService.createFile(image, POST);
		profileImageRepository.save(ProfileImage.builder()
				.url(url)
				.associate(associate)
				.registrant(registrant)
				.build());
	}

	@Transactional
	public void delete(Long communityId, Long associateId, Long registrantId, Long profileImageId) {
		Associate registrant = validAssociate(communityId, registrantId);
		Associate associate = validAssociate(communityId, associateId);

		ProfileImage profileImage = profileImageRepository.findByIdAndDeletedAtIsNull(profileImageId)
			.orElseThrow(() -> new MementoException(ErrorCodes.PROFILEIMAGE_NOT_EXISTENCE));

		if(!profileImage.getRegistrant().equals(registrant) && !profileImage.getAssociate().equals(associate)){
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		profileImage.markDeleted();
	}

	public SearchProfileImageResponse search(Long communityId, Long associateId, Pageable pageable, Long cursor) {
		Associate associate = validAssociate(communityId, associateId);

		List<ProfileImage> profileImageList = profileImageRepository.findProfileImageByAssociateId(associate.getId(), cursor, pageable);

		Long lastCursor = null;
		boolean hasNext = false;
		if(profileImageList.size() == pageable.getPageSize()){
			lastCursor = profileImageList.get(profileImageList.size() - 1).getId();
			hasNext = true;
		}

		List<SearchProfileImageResponse.ProfileImage> profileImages = profileImageList.stream()
			.map(p -> SearchProfileImageResponse.ProfileImage.builder()
				.id(p.getId())
				.url(p.getUrl())
				.build())
			.toList();

		return SearchProfileImageResponse.builder()
			.profileImages(profileImages)
			.cursor(lastCursor)
			.hasNext(hasNext)
			.build();
	}

	// todo 주석처리
	// public String saveImage(Associate associate, MultipartFile image) {
	// 	String bucket = minioProperties.getBucket();
	// 	String baseUrl = minioProperties.getUrl();
	//
	// 	String originalFilename = image.getOriginalFilename();
	// 	String extension = getExtension(originalFilename);
	// 	String filename = "profileImage/"+ associate.getId() +"/" + UUID.randomUUID() + "." + extension;
	// 	String expectedContentType = "image/" + extension;
	//
	// 	try (InputStream inputStream = image.getInputStream()) {
	// 		long contentLength = image.getSize();
	//
	// 		minioClient.putObject(
	// 			PutObjectArgs.builder()
	// 				.bucket(bucket)
	// 				.object(filename)
	// 				.stream(inputStream, contentLength, -1)
	// 				.contentType(expectedContentType)
	// 				.build()
	// 		);
	//
	// 	} catch (Exception e) {
	// 		throw new MementoException(ErrorCodes.PROFILEIMAGE_SAVE_FAIL);
	// 	}
	//
	// 	return baseUrl + "/" + filename;
	// }
}
