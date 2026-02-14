package com.memento.server.api.controller.member.dto;

public record EmailCheckResponse(
        boolean isDuplicate
) {

    public static EmailCheckResponse of(boolean isDuplicate){
        return new EmailCheckResponse(isDuplicate);
    }
}
