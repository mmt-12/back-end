package com.memento.server.api.controller.member.dto;

public record EmailCheckResponse(
        boolean isAvailable
) {

    public static EmailCheckResponse of(boolean isAvailable){
        return new EmailCheckResponse(isAvailable);
    }
}
