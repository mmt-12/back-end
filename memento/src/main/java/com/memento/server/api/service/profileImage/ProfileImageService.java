package com.memento.server.api.service.profileImage;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.memento.server.api.controller.profileImage.dto.SearchProfileImageResponse;
import com.memento.server.api.service.eventMessage.EventMessagePublisher;
import com.memento.server.api.service.eventMessage.dto.NewImageNotification;
import com.memento.server.api.service.minio.MinioService;
import com.memento.server.common.error.ErrorCodes;
import com.memento.server.common.exception.MementoException;
import com.memento.server.config.MinioProperties;
import com.memento.server.domain.community.Associate;
import com.memento.server.domain.community.AssociateRepository;
import com.memento.server.domain.profileImage.ProfileImage;
import com.memento.server.domain.profileImage.ProfileImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileImageService {

	private final AssociateRepository associateRepository;
	private final ProfileImageRepository profileImageRepository;
	private final MinioService minioService;
	private final EventMessagePublisher eventMessagePublisher;

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

		String url = minioService.createFile(image, MinioProperties.FileType.PROFILE_IMAGE);
		profileImageRepository.save(ProfileImage.builder()
				.url(url)
				.associate(associate)
				.registrant(registrant)
				.build());
		eventMessagePublisher.publishNotification(NewImageNotification.from(associateId));
	}

	@Transactional
	public void delete(Long communityId, Long associateId, Long ownerId, Long profileImageId) {
		Associate owner = validAssociate(communityId, ownerId);
		Associate associate = validAssociate(communityId, associateId);

		ProfileImage profileImage = profileImageRepository.findByIdAndDeletedAtIsNull(profileImageId)
			.orElseThrow(() -> new MementoException(ErrorCodes.PROFILEIMAGE_NOT_EXISTENCE));

		if(!profileImage.getRegistrant().equals(associate) && !owner.equals(associate)){
			throw new MementoException(ErrorCodes.ASSOCIATE_NOT_AUTHORITY);
		}

		profileImage.markDeleted();
	}

	public SearchProfileImageResponse search(Long communityId, Long associateId, int size, Long cursor) {
		Associate associate = validAssociate(communityId, associateId);

		Pageable pageable = PageRequest.of(0, size+1);

		List<ProfileImage> profileImageList = profileImageRepository.findProfileImageByAssociateId(associate.getId(), cursor, pageable);

		Long lastCursor = null;
		boolean hasNext = false;
		if(profileImageList.size() == pageable.getPageSize()){
			lastCursor = profileImageList.get(profileImageList.size() - 1).getId();
			hasNext = true;
		}

		List<SearchProfileImageResponse.ProfileImage> profileImages = profileImageList.stream().limit(size)
			.map(p -> SearchProfileImageResponse.ProfileImage.builder()
				.id(p.getId())
				.url(p.getUrl())
				.isRegister(Objects.equals(p.getRegistrant().getId(), associate.getId()))
				.build())
			.toList();

		return SearchProfileImageResponse.builder()
			.profileImages(profileImages)
			.nextCursor(lastCursor)
			.hasNext(hasNext)
			.build();
	}
}
