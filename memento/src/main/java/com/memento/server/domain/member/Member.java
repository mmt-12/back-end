package com.memento.server.domain.member;

import static com.memento.server.domain.member.MemberType.KAKAO;
import static com.memento.server.domain.member.MemberType.NORMAL;
import static com.memento.server.domain.member.MemberType.REJECT;
import static com.memento.server.domain.member.MemberType.WAIT;
import static com.memento.server.utility.validation.member.MemberValidator.validateBirthday;
import static com.memento.server.utility.validation.member.MemberValidator.validateEmail;
import static com.memento.server.utility.validation.member.MemberValidator.validateKakaoId;
import static com.memento.server.utility.validation.member.MemberValidator.validateName;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.memento.server.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
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

	@Column(name = "password", columnDefinition = "CHAR(64)", nullable = true)
	private String password;

	@Column(name = "email", length = 255, unique = true, nullable = false)
	private String email;

	@Column(name = "birthday", nullable = true)
	private LocalDate birthday;

	@Column(name = "kakao_id", nullable = true)
	private Long kakaoId;

	@Enumerated(STRING)
	private MemberType type;

	public static Member createKakao(String name, String email, LocalDate birthday, Long kakaoId) {
		validateName(name);
		validateEmail(email);
		validateBirthday(birthday);
		validateKakaoId(kakaoId);

		return Member.builder()
			.name(name)
			.email(email)
			.birthday(birthday)
			.kakaoId(kakaoId)
			.type(KAKAO)
			.build();
	}

	public static Member createNormal(String name, String password, String email, LocalDate birthday) {
		validateName(name);
		validateEmail(email);
		validateBirthday(birthday);

		return Member.builder()
			.name(name)
			.password(password)
			.email(email)
			.birthday(birthday)
			.type(WAIT)
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

	public void update(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public void signUpApprove() {
		this.type = NORMAL;
	}

	public void signUpReject() {
		this.type = REJECT;
	}
}
