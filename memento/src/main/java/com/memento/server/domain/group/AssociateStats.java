package com.memento.server.domain.group;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import com.memento.server.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "associate_status")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class AssociateStats {

	@Id
	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@Column(name = "consecutive_attendance_days", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int consecutiveAttendanceDays;

	@Column(name = "last_attended_at", nullable = true)
	private LocalDateTime lastAttendedAt;

	@Column(name = "uploaded_reaction_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int uploadedReactionCount;

	@Column(name = "used_reaction_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int usedReactionCount;

	@Column(name = "guest_book_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int guestBookCount;

	@Column(name = "uploaded_profile_image_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int uploadedProfileImageCount;

	@Column(name = "registered_profile_image_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int registeredProfileImageCount;

	@Column(name = "uploaded_post_image_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int uploadedPostImageCount;

	@Column(name = "created_memory_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int createdMemoryCount;

	@Column(name = "joined_memory_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int joinedMemoryCount;

	@Column(name = "mbti_test_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int mbtiTestCount;

	@Column(name = "f_mbti_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int fMbtiCount;

	@Column(name = "t_mbti_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private int tMbtiCount;
}
