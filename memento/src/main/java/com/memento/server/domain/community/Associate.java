package com.memento.server.domain.community;

import static com.memento.server.utility.validation.associate.AssociateValidator.*;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;
import com.memento.server.domain.achievement.Achievement;
import com.memento.server.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "associates")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Associate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "nickname", length = 51, nullable = false)
	private String nickname;

	@Column(name = "profile_image_url", length = 255, nullable = true)
	private String profileImageUrl;

	@Column(name = "introduction", length = 255, nullable = true)
	private String introduction;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "achievement_id", nullable = true, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Achievement achievement;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "community_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Community community;

	public static Associate create(String nickname, Member member, Community community) {
		validateNickname(nickname);
		validateMember(member);
		validateCommunity(community);

		return Associate.builder()
			.nickname(nickname)
			.member(member)
			.community(community)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Associate associate)) {
			return false;
		}
		return id != null && Objects.equals(id, associate.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	public void updateProfile(
		String ProfileImageUrl,
		String Nickname,
		Achievement Achievement,
		String Introduction) {
		this.profileImageUrl = ProfileImageUrl;
		this.nickname = Nickname;
		this.achievement = Achievement;
		this.introduction = Introduction;
	}
}
