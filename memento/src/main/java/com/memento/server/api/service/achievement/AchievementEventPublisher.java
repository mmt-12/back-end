package com.memento.server.api.service.achievement;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.memento.server.domain.achievement.CommonAchievementEvent;
import com.memento.server.domain.community.AssociateExclusiveAchievementEvent;
import com.memento.server.domain.community.SignInAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookAchievementEvent;
import com.memento.server.domain.guestBook.GuestBookExclusiveAchievementEvent;
import com.memento.server.domain.mbti.MbtiAchievementEvent;
import com.memento.server.domain.memory.MemoryAchievementEvent;
import com.memento.server.domain.post.PostImageAchievementEvent;
import com.memento.server.domain.profileImage.ProfileImageAchievementEvent;
import com.memento.server.domain.reaction.ReactionAchievementEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AchievementEventPublisher {

	private final ApplicationEventPublisher publisher;

	public void publishCommonAchievement(CommonAchievementEvent event){ publisher.publishEvent(event);}

	public void publishProfileImageAchievement(ProfileImageAchievementEvent event){
		publisher.publishEvent(event);
	}

	public void publishMbtiAchievement(MbtiAchievementEvent event){
		publisher.publishEvent(event);
	}

	public void publishGuestBookAchievement(GuestBookAchievementEvent event){
		publisher.publishEvent(event);
	}

	public void publishMemoryAchievement(MemoryAchievementEvent event){ publisher.publishEvent(event);}

	public void publishPostImageAchievement(PostImageAchievementEvent event){ publisher.publishEvent(event);}

	public void publishReactionAchievement(ReactionAchievementEvent event){ publisher.publishEvent(event);}

	public void publishSignInAchievement(SignInAchievementEvent event){ publisher.publishEvent(event);}

	public void publishGuestBookExclusiveAchievement(GuestBookExclusiveAchievementEvent event){ publisher.publishEvent(event);}

	public void publishAssociateExclusiveAchievement(AssociateExclusiveAchievementEvent event){ publisher.publishEvent(event);}
}
