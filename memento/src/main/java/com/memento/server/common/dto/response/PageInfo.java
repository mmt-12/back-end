package com.memento.server.common.dto.response;

import lombok.Builder;

@Builder
public record PageInfo(
	boolean hasNext,
	Long nextCursor
) {

	public static PageInfo of(boolean hasNext, Long nextCursor) {
		return PageInfo.builder().hasNext(hasNext).nextCursor(nextCursor).build();
	}
}
