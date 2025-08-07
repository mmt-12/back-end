package com.memento.server.domain.member;

import static com.memento.server.utility.validation.member.MemberValidator.*;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "members")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(name = "name", length = 102, nullable = false)
	private String name;

	@Column(name = "email", length = 255, nullable = false)
	private String email;

	@Column(name = "birthday", nullable = true)
	private LocalDate birthday;

	@Column(name = "kakao_id", nullable = false)
	private Long kakaoId;

	public static Member create(String name, String email, LocalDate birthday, Long kakaoId) {
		validateName(name);
		validateEmail(email);
		validateBirthday(birthday);
		validateKakaoId(kakaoId);

		return Member.builder()
			.name(name)
			.email(email)
			.birthday(birthday)
			.kakaoId(kakaoId)
			.build();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Member member)) {
			return false;
		}
		return id != null && Objects.equals(id, member.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
