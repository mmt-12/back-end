package com.memento.server.domain.post;

import static lombok.AccessLevel.PROTECTED;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Builder
public class Hash {

	@Column(name = "hash", length = 255, nullable = false)
	private String hash;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Hash)) return false;
		Hash other = (Hash) o;
		return Objects.equals(this.hash, other.hash);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hash);
	}
}
