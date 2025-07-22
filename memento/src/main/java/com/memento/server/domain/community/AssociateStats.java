package com.memento.server.domain.community;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "associate_stats")
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class AssociateStats {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@OneToOne(fetch = LAZY)
	@JoinColumn(name = "associate_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Associate associate;

	@Column(name = "consecutive_attendance_days", nullable = false)
	private int consecutiveAttendanceDays;

	@Column(name = "last_attended_at", nullable = true)
	private LocalDateTime lastAttendedAt;

	@Column(name = "uploaded_reaction_count", nullable = false)
	private int uploadedReactionCount;

	@Column(name = "used_reaction_count", nullable = false)
	private int usedReactionCount;

	@Column(name = "guest_book_count", nullable = false)
	private int guestBookCount;

	@Column(name = "uploaded_profile_image_count", nullable = false)
	private int uploadedProfileImageCount;

	@Column(name = "registered_profile_image_count", nullable = false)
	private int registeredProfileImageCount;

	@Column(name = "uploaded_post_image_count", nullable = false)
	private int uploadedPostImageCount;

	@Column(name = "created_memory_count", nullable = false)
	private int createdMemoryCount;

	@Column(name = "joined_memory_count", nullable = false)
	private int joinedMemoryCount;

	@Column(name = "mbti_test_count", nullable = false)
	private int mbtiTestCount;

	@Column(name = "f_mbti_count", nullable = false)
	private int fMbtiCount;

	@Column(name = "t_mbti_count", nullable = false)
	private int tMbtiCount;
}
