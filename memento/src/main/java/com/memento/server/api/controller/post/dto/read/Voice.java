package com.memento.server.api.controller.post.dto.read;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
public class Voice extends Reaction{
	boolean isInvolved;
}
